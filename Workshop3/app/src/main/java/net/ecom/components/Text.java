package net.ecom.components;

import net.ecom.ui.Ui;

public class Text extends UiComponent<Text> {
    private String content = "";

    public Text() {
        super(0, 0);
    }

    public Text(String content) {
        super(0, 0);
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Text updateUi(ComponentUpdate<Text> f) {
        f.update(this);
        return this;
    }

    public void createUi(Ui ui) {
        ui.drawText(this.getContent(), this.getX(), this.getY());
    }
}
