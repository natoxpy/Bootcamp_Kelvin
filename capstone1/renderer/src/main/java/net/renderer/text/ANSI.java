package net.renderer.text;

public enum ANSI {
    RESET(0),
    BOLD(1),
    ITALIC(3),
    UNDERLINE(4),

    BLACK(30, 40),
    RED(31, 41),
    GREEN(32, 42),
    YELLOW(33, 43),
    BLUE(34, 44),
    MAGENTA(35, 45),
    CYAN(36, 46),
    WHITE(37, 47),

    BRIGHT_BLACK(90, 100),
    BRIGHT_RED(91, 101),
    BRIGHT_GREEN(92, 102),
    BRIGHT_YELLOW(93, 103),
    BRIGHT_BLUE(94, 104),
    BRIGHT_MAGENTA(95, 105),
    BRIGHT_CYAN(96, 106),
    BRIGHT_WHITE(97, 107);

    public final int fg;
    public final int bg;

    ANSI(int code) {
        this.fg = code;
        this.bg = -1;
    }

    ANSI(int fg, int bg) {
        this.fg = fg;
        this.bg = bg;
    }

    public String fgSeq() {
        return "\033[" + fg + "m";
    }

    public String bgSeq() {
        if (bg < 0)
            throw new IllegalStateException(this + " has no background code");
        return "\033[" + bg + "m";
    }

    public static final String RESET_SEQ = "\033[0m";
}
