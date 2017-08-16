package am.englet;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

public class Trace {
    public static boolean TRACE = false;

    private static PrintStream ps;

    private static String lastCommand;

    public static void command(final String string) {
        lastCommand = string;
    }

    public static void close() {
        ps.close();
        ps = null;
    }

    public static void direct(final Object o) {
        if (TRACE)
            constant(o, "Direct");
    }

    public static Object directObject(final Object o) {
        direct(o);
        // if (TRACE)
        // constant(o, "Unfound");
        return o;
    }

    public static String unfoundString(final String s) {
        if (TRACE)
            constant(s, "Unfound");
        return s;
    }

    public static Object unfound(final Object o) {
        if (TRACE)
            constant(o, "Unfound");
        return o;
    }

    public static Object got(final Object o, final String varname) {
        if (TRACE)
            constant(o, "From var: " + varname);
        return o;
    }

    public static void fail(final Object target, final Object[] args,
            final String command, final Invokable invokable) {
        call("", "fail", "", target, args, command, invokable);
    }

    public static void fail(final Object target, final Object[] args,
            final Invokable invokable) {
        call("", "fail", "", target, args, lastCommand, invokable);
    }

    public static void success(final Object object, final Object target,
            final Object[] args, final String command, final Invokable invokable) {
        final boolean b = object instanceof ResultList;
        if (!b)
            call("@", (b ? ((ResultList) object).content()
                    : (Object) hash(object)), (b ? " @" : ""), target, args,
                    command, invokable);

    }

    public static void success(final Object object, final Object target,
            final Object[] args, final Invokable invokable) {
        success(object, target, args, lastCommand, invokable);
    }

    private static void call(final String prefix1, final Object object,
            final String infix1, final Object target, final Object[] args,
            final String command, final Invokable invokable) {
        final boolean b = args.length > 0;
        objects(new Object[] {
                prefix1,
                object,
                infix1,
                " <- ",
                (invokable.targetType() != null ? new Object[] { "@",
                        hash(target) } : new Object[0]), "", ". ",
                (b ? new Object[] { "@" } : new Object[0]), "",
                (b ? hexHash(args) : new Object[0]), " @", ": ", command, " ",
                toString(invokable) });
    }

    private static Object[] hexHash(final Object[] args) {
        final int length = args.length;
        final Object[] args2 = new Object[length];
        for (int i = 0; i < length; i++)
            args2[i] = hash(args[i]);
        return args2;
    }

    private static Object toString(final Invokable invokable) {
        try {
            return invokable.toString();
        } catch (final Exception e) {
            return new Object[0];
        }
    }

    private static void constant(final Object o, final String string) {
        objects(new Object[] { "@", hash(o), " ", string, ": ",
                (o != null ? o.getClass() : null), ": ",
                (o instanceof String ? saveConvert((String) o, false) : o) });
    }

    public static void objects(final Object[] oo) {
        if (!TRACE)
            return;
        open();
        ps.print("<TRC>");
        final int length1 = oo.length;
        for (int i = 0; i < length1;) {
            final Object obj = oo[i++];
            Class cl;
            if ((obj != null) && (cl = obj.getClass()).isArray()
                    && !cl.getComponentType().isPrimitive()) {
                final Object[] ooo = (Object[]) obj;
                final int length = ooo.length;
                final Object separator = i < length1 - 1 ? oo[i++] : "";
                if (length > 0)
                    ps.print(ooo[0]);
                for (int j = 1; j < length; j++) {
                    ps.print(separator);
                    ps.print(ooo[j]);
                }
            } else
                ps.print(obj);
        }
        Utils.outPrintln(ps, "</TRC>");
        ps.flush();
    }

    private static void open() {
        loop: {
            int n = 0;
            if ((ps == null) || ps.checkError())
                while (true)
                    try {
                        ps = new PrintStream(new FileOutputStream(
                                "englet.trace"
                                        + (n > 0 ? Integer.toString(n) : ""),
                                true));
                        ps.println();
                        ps.print("<RUN>");
                        ps.print(timeString(System.currentTimeMillis()));
                        ps.print("</RUN>");
                        ps.println();
                        break loop;
                    } catch (final Exception e) {
                        n++;
                    }
        }
    }

    private static String timeString(final long millis) {
        final Date date = new Date(millis);
        final int y = date.getYear() + 1900, yy = y % 100;
        final int min = date.getMinutes();
        final int sec = date.getSeconds();
        return Integer.toString(y / 100, 36) + hexDigit[yy / 10]
                + hexDigit[yy % 10] + hexDigit[date.getMonth() + 1]
                + Integer.toString(date.getDate(), 36) + '_'
                + Integer.toString(date.getHours(), 36) + hexDigit[min / 10]
                + hexDigit[min % 10] + hexDigit[sec / 10] + hexDigit[sec % 10];
    }

    private static Object hash(final Object o) {
        return toHexString(System.identityHashCode(o));
    }

    private static String toHexString(int number) {
        final char[] cc = new char[8];
        for (int i = 8; i-- > 0; number >>= 4)
            cc[i] = hexDigit[number & 15];
        return new String(cc);
    }

    /** A table of hex digits */
    private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private static String saveConvert(final String theString,
            final boolean escapeSpace) {
        final int len = theString.length();
        final StringBuffer outBuffer = new StringBuffer(len * 2);

        for (int x = 0; x < len; x++) {
            final char aChar = theString.charAt(x);
            switch (aChar) {
            case ' ':
                if ((x == 0) || escapeSpace)
                    outBuffer.append('\\');
                outBuffer.append(' ');
                break;
            case '\\':
                outBuffer.append('\\');
                outBuffer.append('\\');
                break;
            case '\t':
                outBuffer.append('\\');
                outBuffer.append('t');
                break;
            case '\n':
                outBuffer.append('\\');
                outBuffer.append('n');
                break;
            case '\r':
                outBuffer.append('\\');
                outBuffer.append('r');
                break;
            case '\f':
                outBuffer.append('\\');
                outBuffer.append('f');
                break;
            default:
                if ((aChar < 0x0020) || (aChar > 0x007e)) {
                    outBuffer.append('\\');
                    outBuffer.append('u');
                    outBuffer.append(hexDigit[((aChar >> 12) & 0xF & 0xF)]);
                    outBuffer.append(hexDigit[((aChar >> 8) & 0xF & 0xF)]);
                    outBuffer.append(hexDigit[((aChar >> 4) & 0xF & 0xF)]);
                    outBuffer.append(hexDigit[(aChar & 0xF & 0xF)]);
                } else {
                    if ("=: \t\r\n\f#!".indexOf(aChar) != -1)
                        outBuffer.append('\\');
                    outBuffer.append(aChar);
                }
            }
        }
        return outBuffer.toString();
    }

    public static void log() {
        // TODO Auto-generated method stub

    }

}
