package net.ecom.views;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import net.ecom.Model;
import net.ecom.components.Text;
import net.ecom.components.UiComponent;
import net.ecom.components.Window;
import net.ecom.ui.Ui;
import org.jline.terminal.Size;

public class MainView {
    private Ui root;
    private UiComponent<Window> body;
    private Window footer;
    private SidebarView sidebar;
    private ProductDisplayView productDisplay;

    public MainView(Size size) {
        this.root = new Ui(size);

        int rootWidth = size.getColumns();
        int rootHeight = size.getRows();

        this.body =
            new Window(120, 30)
                .updateUi((c) -> c.setX(UiComponent.getCenter(rootWidth, ((Window) c).getW())))
                .updateUi((c) -> c.setY(UiComponent.getCenter(rootHeight, ((Window) c).getH())));

        this.sidebar = new SidebarView(body);
        this.productDisplay = new ProductDisplayView(body);

        this.footer = (Window) new Window()
                          .updateUi((c) -> c.setW(((Window) body).getW()))
                          .updateUi((c) -> c.setH(3))
                          .updateUi(UiComponent.SetPosition(-1, ((Window) body).getH() - 2));

        this.body.addChildren(footer);
    }

    public void updateFooter(Model model) {
        this.footer.clearChildren();
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> b : model.getFocusBehaviors().entrySet()) {
            sb.append(String.format("%s - %s", b.getKey(), b.getValue()));
            sb.append(String.format("%-5s", ""));
        }

        Text txt = new Text(sb.toString());
		txt.updateUi(UiComponent.SetX(UiComponent.getCenter(this.footer.getW(), txt.getContent().length())));

        this.footer.addChildren(txt);
    }

    public void update(Model model) {
        this.root.getContext().clearContext();

        this.sidebar.update(model);
        this.productDisplay.update(model);
        this.updateFooter(model);

        this.body.createUi(this.root);
    }

    public void render(Writer writer) {
        String[] lines = this.root.getContext().getLines();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            try {
                writer.write(line);
                if (i < lines.length - 1)
                    writer.write("\r\n");
            } catch (IOException e) {
                throw new RuntimeException("Unable to View due to error while running.");
            }
        }
    }
}
