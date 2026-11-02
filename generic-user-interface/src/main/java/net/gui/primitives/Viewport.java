package net.gui.primitives;

public record Viewport(int offsetX, int offsetY, Integer child)
    implements Node {}
