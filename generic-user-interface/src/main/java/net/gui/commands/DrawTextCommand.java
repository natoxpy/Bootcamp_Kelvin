package net.gui.commands;

public record DrawTextCommand(int x, int y, String content) implements DrawCommand {}
