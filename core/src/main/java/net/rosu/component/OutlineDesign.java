package net.rosu.component;

public record OutlineDesign(char top, char bottom, char Left, char Right, char topLeft,
    char topRight, char bottomLeft, char bottomRight) {
    public OutlineDesign(
        char top, char left, char topLeft, char topRight, char bottomLeft, char bottomRight) {
        this(top, top, left, left, topLeft, topRight, bottomLeft, bottomRight);
    }

    public static OutlineDesign basic() {
        return new OutlineDesign('─', '│', '┌', '┐', '└', '┘');
    }

    public static OutlineDesign basicTopConnect() {
        return new OutlineDesign('─', '│', '├', '┤', '└', '┘');
    }

    public static OutlineDesign basicBottomConnect() {
        return new OutlineDesign('─', '│', '┌', '┐', '├', '┤');
    }
}
