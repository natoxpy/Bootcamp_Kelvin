package net.example.demo;

import net.rosu.Rosu;
import net.rosu.component.Component;
import net.rosu.component.InputComponent;
import net.rosu.component.Renderable;

class Window extends Component {
	int counter = 0;

    @Override
    public Renderable render() {
        return new InputComponent();
    }

	@Override
	public void onKey(char c) {
		this.redraw();
		this.counter += 1;
	}
}

public class Demo {
    public static void main(String[] args) {
        Rosu app = new Rosu();

        Component window = new Window();

        app.setRoot(window);

        app.start();
        app.close();
    }
}
