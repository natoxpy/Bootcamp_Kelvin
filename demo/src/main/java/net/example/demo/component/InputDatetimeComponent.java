package net.example.demo.component;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.gui.Context;
import net.gui.primitives.Rect;
import net.gui.primitives.dimension.Auto;
import net.gui.primitives.dimension.Fixed;
import net.rosu.IdAware;
import net.rosu.Tickable;
import net.rosu.component.BoxComponent;
import net.rosu.component.Clickable;
import net.rosu.component.Component;
import net.rosu.component.Focusable;
import net.rosu.component.Renderable;
import net.rosu.component.TextComponent;

public class InputDatetimeComponent
    extends Component implements Focusable, Tickable, IdAware, Clickable {
    private static final class Segment {
        final String label;
        final int length;
        final int min;
        final int max;
        String buffer = "";

        Segment(String label, int length, int min, int max) {
            this.label = label;
            this.length = length;
            this.min = min;
            this.max = max;
        }

        boolean isFull() {
            return buffer.length() == length;
        }
    }

    public static record PartialDateTime(
        Integer year, Integer month, Integer day, Integer hour, Integer minute) {
        public LocalDateTime toEarliestBound() {
            int y = year != null ? year : Year.MIN_VALUE;
            int mo = month != null ? month : 1;
            int d = day != null ? day : 1;
            int h = hour != null ? hour : 0;
            int mi = minute != null ? minute : 0;
            return LocalDateTime.of(y, mo, d, h, mi);
        }

        public LocalDateTime toLatestBound() {
            int y = year != null ? year : Year.MAX_VALUE;
            int mo = month != null ? month : 12;
            int d = day != null ? day : YearMonth.of(y, mo).lengthOfMonth();
            int h = hour != null ? hour : 23;
            int mi = minute != null ? minute : 59;
            return LocalDateTime.of(y, mo, d, h, mi);
        }
    }

    private final List<Segment> segments = List.of(new Segment("____", 4, 1, 9999), // YYYY
        new Segment("__", 2, 1, 12), // MM
        new Segment("__", 2, 1, 31), // DD
        new Segment("__", 2, 0, 23), // HH
        new Segment("__", 2, 0, 59) // mm
    );

    private final String[] separators = {"-", "-", " ", ":"}; // YYYY-MM-DD HH:mm

    private final int[] segmentStartOffset;
    private final int totalWidth;

    private int activeSegment = 0;
    private int cursorInSegment = 0;

    private Context ctx;
    private Integer id;
    private boolean cursorVisible = true;
    private long lastBlinkToggle = System.currentTimeMillis();
    private static final long BLINK_INTERVAL_MS = 500;
    private Consumer<PartialDateTime> onPartialDT = null;

    public InputDatetimeComponent() {
        segmentStartOffset = new int[segments.size()];
        int offset = 0;
        for (int i = 0; i < segments.size(); i++) {
            segmentStartOffset[i] = offset;
            offset += segments.get(i).length;
            if (i < separators.length)
                offset += separators[i].length();
        }
        totalWidth = offset;
    }

    @Override
    public Renderable render() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < segments.size(); i++) {
            Segment seg = segments.get(i);
            String display = displayFor(seg);

            if (i == activeSegment && isFocused() && cursorVisible) {
                if (cursorInSegment >= display.length()) {
                    // cursor sitting just past the last char (segment full, nothing to overwrite)
                    display = display.substring(0, display.length() - 1) + "│";
                } else {
                    display = display.substring(0, cursorInSegment) + "│"
                        + display.substring(cursorInSegment + 1);
                }
            }

            sb.append(display);
            if (i < separators.length)
                sb.append(separators[i]);
        }

        return new BoxComponent(new TextComponent(sb.toString()))
            .setHeight(new Auto())
            .setWidth(new Fixed(totalWidth + 1)); // +1 to leave room for the cursor glyph
    }

    public InputDatetimeComponent onPartialDt(Consumer<PartialDateTime> event) {
        this.onPartialDT = event;
        return this;
    }

    private String displayFor(Segment seg) {
        if (seg.buffer.isEmpty())
            return seg.label; // hint text, e.g. "MM"
        StringBuilder sb = new StringBuilder(seg.buffer);
        while (sb.length() < seg.length) sb.append('_');
        return sb.toString();
    }

    @Override
    public final void requestFocus() {
        this.lastBlinkToggle = System.currentTimeMillis() + BLINK_INTERVAL_MS;
        super.requestFocus();
    }

    @Override
    public void onKey(char key) {
        if (key == '\b' || key == 127) {
            resetBlink();
            backspace();
            return;
        }

        if (key == '/' || key == '-' || key == ':') {
            advanceSegment();
            resetBlink();
            return;
        }

        if (Character.isDigit(key)) {
            insertDigit(key);
            resetBlink();
        }
    }

    private void insertDigit(char key) {
        Segment seg = segments.get(activeSegment);
        if (cursorInSegment >= seg.length)
            return;

        String candidate;
        if (cursorInSegment < seg.buffer.length()) {
            candidate = seg.buffer.substring(0, cursorInSegment) + key
                + seg.buffer.substring(cursorInSegment + 1);
        } else {
            candidate = seg.buffer + key;
        }

        if (!partiallyValid(candidate, seg.length, seg.min, seg.max))
            return;

        seg.buffer = candidate;
        cursorInSegment++;
        redraw();

        if (seg.isFull()) {
            if (activeSegment < segments.size() - 1) {
                activeSegment++;
                cursorInSegment = 0;
            } else {
                // last segment just completed
                this.defocus();
            }
            redraw();
        }

        this.notifyPartialDT();
    }

    private void notifyPartialDT() {
        if (this.onPartialDT != null)
            this.onPartialDT.accept(this.getPartialDt());
    }

    /** Can `partial` still expand (via appended digits) into a value within [min, max]? */
    private boolean partiallyValid(String partial, int length, int min, int max) {
        int remaining = length - partial.length();
        long lower = Long.parseLong(partial + "0".repeat(remaining));
        long upper = Long.parseLong(partial + "9".repeat(remaining));
        return upper >= min && lower <= max;
    }

    private void backspace() {
        Segment seg = segments.get(activeSegment);

        if (cursorInSegment > 0) {
            seg.buffer = seg.buffer.substring(0, cursorInSegment - 1)
                + seg.buffer.substring(cursorInSegment);
            cursorInSegment--;
            redraw();
            this.notifyPartialDT();
            return;
        }

        if (activeSegment > 0) {
            activeSegment--;
            segments.get(activeSegment).buffer = "";
            cursorInSegment = 0;
            redraw();
            this.notifyPartialDT();
        }
    }

    private void advanceSegment() {
        if (activeSegment < segments.size() - 1) {
            activeSegment++;
            cursorInSegment = 0;
            redraw();
        }
    }

    private void resetBlink() {
        cursorVisible = true;
        lastBlinkToggle = System.currentTimeMillis();
    }

    @Override
    public void tick(long now) {
        if (!isFocused())
            return;

        if (now - lastBlinkToggle >= BLINK_INTERVAL_MS) {
            cursorVisible = !cursorVisible;
            lastBlinkToggle = now;
            redraw();
        }
    }

    @Override
    public void onIdAssigned(Context ctx, int id) {
        this.ctx = ctx;
        this.id = id;
    }

    @Override
    public void click(int x, int y) {
        Rect r = this.ctx.drawtime.getRect(this.id);
        if (r == null)
            return;

        if (!r.contains(x, y - 1)) {
            this.defocus();
            resetBlink();
            redraw();
            return;
        }

        int col = Math.max(0, Math.min(x - r.x(), totalWidth - 1));

        int target = 0;
        for (int i = 0; i < segments.size(); i++) {
            int start = segmentStartOffset[i];
            int end = start + segments.get(i).length;

            if (col < start) {
                // click landed in the separator before this segment — snap to whichever
                // side of the gap is closer (previous segment's end vs this segment's start)
                int prevEnd = segmentStartOffset[i - 1] + segments.get(i - 1).length;
                target = (col - prevEnd <= start - col) ? i - 1 : i;
                break;
            }

            if (col < end) {
                target = i;
                break;
            }

            target = i; // in case col is past every segment, this leaves target at the last one
        }

        Segment target_seg = segments.get(target);
        target_seg.buffer = "";
        this.notifyPartialDT();

        activeSegment = target;
        cursorInSegment = 0;
        requestFocus();
        resetBlink();
        redraw();
    }

    public boolean isComplete() {
        return segments.stream().allMatch(Segment::isFull);
    }

    public Optional<LocalDateTime> getValue() {
        if (!isComplete())
            return Optional.empty();

        try {
            int month = Integer.parseInt(segments.get(0).buffer);
            int day = Integer.parseInt(segments.get(1).buffer);
            int year = Integer.parseInt(segments.get(2).buffer);
            int hour = Integer.parseInt(segments.get(3).buffer);
            int minute = Integer.parseInt(segments.get(4).buffer);
            return Optional.of(LocalDateTime.of(year, month, day, hour, minute));
        } catch (DateTimeException | NumberFormatException e) {
            return Optional.empty(); // shape was right, calendar value wasn't (e.g. Feb 30)
        }
    }

    public PartialDateTime getPartialDt() {
        return new PartialDateTime(parseSegment(segments.get(2)), // year
            parseSegment(segments.get(0)), // month
            parseSegment(segments.get(1)), // day
            parseSegment(segments.get(3)), // hour
            parseSegment(segments.get(4)) // minute
        );
    }

    private Integer parseSegment(Segment seg) {
        if (seg.buffer.isEmpty())
            return null;
        try {
            return Integer.parseInt(seg.buffer);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
