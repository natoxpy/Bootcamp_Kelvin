package net.example.demo;

import net.rosu.Rosu;
import net.rosu.component.Component;
import net.rosu.component.Focusable;
import net.rosu.component.Renderable;
import net.rosu.component.TextComponent;

class Inner extends Component implements Focusable {
    int count = 0;

    public void setCount(int count) {
        this.count = count;
        redraw();
    }

    @Override
    public Renderable render() {
        return new TextComponent(String.format("Hello, Paimon %s", this.count));
    }

    @Override
    public void onKey(char key) {
		System.out.println(count);
		this.setCount(count + 1);
    }
}

class Window extends Component {
    @Override
    public Renderable render() {
        return new Inner();
    }

    public void onKey(char c) {}
}

public class Demo {
    public static void main(String[] args) {
        Rosu app = new Rosu();

        Component window = new Window();

        app.setRoot(window);
        app.setFocus(window);

        app.start();
		app.close();
    }
}
