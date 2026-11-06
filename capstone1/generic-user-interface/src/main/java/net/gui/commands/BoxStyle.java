package net.gui.commands;

public record BoxStyle(Color backgroundColor, Color outlineColor) {
    public BoxStyle(Color backgroundColor) {
        this(backgroundColor, null);
    }
}
