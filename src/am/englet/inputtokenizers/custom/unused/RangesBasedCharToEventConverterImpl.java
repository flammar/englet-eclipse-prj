package am.englet.inputtokenizers.custom.unused;

import java.util.Comparator;
import java.util.TreeMap;

// TODO significantly incomplete
public class RangesBasedCharToEventConverterImpl implements CharToEventConverter {

    private final TreeMap startMap = new TreeMap(Range.START_COMPARATOR);
    private final TreeMap endMap = new TreeMap(Range.END_COMPARATOR);

    public String event(final char ch) {
        final Integer integer = new Integer(ch);
        startMap.headMap(integer);
        // TODO Auto-generated method stub
        return null;
    }

    static class Range {
        public final char from;// incl
        public final char to;// incl
        public final String value;

        /**
         * @param from
         * @param to
         * @param value
         */
        public Range(final char from, final char to, final String value) {
            this.from = from;
            this.to = to;
            this.value = value;
        }

        public static Comparator START_COMPARATOR = new Comparator() {
            public int compare(final Object o1, final Object o2) {
                final char from2 = o1 instanceof Range ? ((Range) o1).from : (char) ((Number) o1).intValue();
                final char from3 = o2 instanceof Range ? ((Range) o2).from : (char) ((Number) o2).intValue();
                return from2 - from3;
            }
        };

        public static Comparator END_COMPARATOR = new Comparator() {
            public int compare(final Object o1, final Object o2) {
                final char from2 = o1 instanceof Range ? ((Range) o1).to : (char) ((Number) o1).intValue();
                final char from3 = o2 instanceof Range ? ((Range) o2).to : (char) ((Number) o2).intValue();
                return from2 - from3;
            }
        };
    }
}
