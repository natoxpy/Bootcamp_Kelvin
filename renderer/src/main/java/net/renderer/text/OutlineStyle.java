package net.renderer.text;

public record OutlineStyle(char top, char bottom, char Left, char Right, char topLeft,
    char topRight, char bottomLeft, char bottomRight) {
    public OutlineStyle(
        char top, char left, char topLeft, char topRight, char bottomLeft, char bottomRight) {
        this(top, top, left, left, topLeft, topRight, bottomLeft, bottomRight);
    }
}
