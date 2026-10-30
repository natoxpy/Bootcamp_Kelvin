package net.ecom;

import net.ecom.views.MainView;

public class Controller {
    private MainView view;
    private Model model;

    public Controller(MainView view, Model model) {
        this.view = view;
        this.model = model;
    }

    public MainView getView() {
        return view;
    }

    public Model getModel() {
        return model;
    }

    public void handleInput(int c) {
        if (c == 'n') {
			this.model.setTitle("Hello");
        }
    }
}
