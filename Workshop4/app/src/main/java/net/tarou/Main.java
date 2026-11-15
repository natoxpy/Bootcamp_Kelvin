package net.tarou;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.tarou.api.Api;
import net.tarou.db.Database;
import net.tarou.db.DealershipDao;
import netscape.javascript.JSObject;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlite3.SQLitePlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class Main extends Application {
    private static Path seedDatabase() throws IOException {
        Path target = Path.of("data", "app.db").toAbsolutePath();
        Files.createDirectories(target.getParent());
        return target;
    }

    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        Path p;

        try {
            p = Main.seedDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Jdbi jdbi = Jdbi.create("jdbc:sqlite:" + p)
                        .installPlugin(new SQLitePlugin())
                        .installPlugin(new SqlObjectPlugin());

        // jdbi.useExtension(DealershipDao.class, dao -> { dao.createTable(); });

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState != Worker.State.SUCCEEDED)
                return;

            JSObject window = (JSObject) engine.executeScript("window");
            window.setMember("api", new Api(new Database(jdbi)));
        });

        String devUrl = System.getenv("DEV_URL");
        if (devUrl != null && !devUrl.isBlank()) {
            engine.load(devUrl);
        } else {
            engine.load(getClass().getResource("/web/index.html").toExternalForm());
        }

        stage.setScene(new Scene(webView, 800, 600));
        stage.setTitle("TSS");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
