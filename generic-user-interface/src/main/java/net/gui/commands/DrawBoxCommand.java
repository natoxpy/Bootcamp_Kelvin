package net.gui.commands;

public record DrawBoxCommand(int x, int y, int width, int height) implements DrawCommand {}
