package am.englet.inputtokenizers.custom.unused;

import java.util.Arrays;

import am.englet.Utils;

public class ArrayBasedCharToEventConverterImpl implements CharToEventConverter {
    private final String defaultEvent;
    private String[] values = null;
    private int start;

    /**
     * @param defaultEvent
     */
    public ArrayBasedCharToEventConverterImpl(final String defaultEvent) {
        this.defaultEvent = defaultEvent;
    }

    public String event(final char ch) {
        if ((values == null))
            return defaultEvent;
        final int dif = ch - start;
        if ((dif < 0) || (dif >= values.length))
            return defaultEvent;
        return values[dif];
    }

    public void setEvent(final CharSequence chars, final String value) {
        set(chars.charAt(0), chars.charAt(1), value);
    }

    public void set(final CharSequence chars) {
        set(chars, chars.toString());
    }

    public void set(final CharSequence chars, final String value) {
        set(chars.charAt(0), chars.charAt(chars.length() > 1 ? 1 : 0), value);
    }

    public void set(final char from, final char to, final String value) {
        if (values == null)
            Arrays.fill(values = new String[to + 1 - (start = from)], value);
        else {
            final int start0 = start;
            final int end0 = start0 + values.length;
            if (from < start0) {
                final String[] newvals = new String[end0 - from];
                System.arraycopy(values, 0, newvals, start0 - from,
                        values.length);
                values = newvals;
                start = from;
            }
            if (to + 1 > end0) {
                final String[] newvals = new String[to + 1 - start];
                System.arraycopy(values, 0, newvals, 0, values.length);
                values = newvals;
            }
            final int toIndex = to + 1;
            Arrays.fill(values, from - start, toIndex - start, value);
            if (toIndex < start0)
                Arrays.fill(values, toIndex - start, start0 - start,
                        defaultEvent);
            if (from > end0)
                Arrays.fill(values, end0 - start, from - start, defaultEvent);
        }
    }

    public String dump() {
        final StringBuffer sb = new StringBuffer("[default]=").append(
                defaultEvent).append("; ");
        if (values != null)
            for (int i = 0; i < values.length; i++)
                sb.append('[').append(i + start).append("]=").append(values[i])
                        .append("; ");
        return sb.toString();
    }

    public static void main(final String[] args) {
        final ArrayBasedCharToEventConverterImpl t = new ArrayBasedCharToEventConverterImpl(
                "defaultEvent");
        Utils.outPrintln(System.out, t.dump());
        t.set((char) 10, (char) 15, "10..15");
        Utils.outPrintln(System.out, t.dump());
        t.set((char) 11, (char) 13, "11..13");
        Utils.outPrintln(System.out, t.dump());
        t.set((char) 9, (char) 11, "9..11");
        Utils.outPrintln(System.out, t.dump());
        t.set((char) 15, (char) 17, "15..17");
        Utils.outPrintln(System.out, t.dump());
        t.set((char) 1, (char) 2, "1..2");
        Utils.outPrintln(System.out, t.dump());
        t.set((char) 21, (char) 23, "21..23");
        Utils.outPrintln(System.out, t.dump());
    }
}
