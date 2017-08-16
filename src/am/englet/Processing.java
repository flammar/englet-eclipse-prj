/**
 *
 */
package am.englet;

import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import am.englet.Links.NextContentProvider;
import am.englet.MethodsStorage.Direct;
import am.englet.MethodsStorage.Getter;
import am.englet.link.BackAdapter;
import am.englet.link.BackUsageStrategy;
import am.englet.link.FinalLink;
import am.englet.link.Link;
import am.englet.link.SimpleLinkFactory;
import am.englet.link.backadapters.IteratorAdapter;
import am.englet.link.backadapters.IteratorStrategy;
import am.englet.link.backadapters.slider.LinkSlider;

/**
 * @author Adm1
 * 
 */
public class Processing {

    public static final String METHOD_NAME_REPLACEMENTS = "" + "fromClip -c "
            + "fromBytes -b fromUrl -u fromFile -f fromFileo -fo toClip $c toFile $f toFileo $fo appendToFile $a "
            + "part @ replace $r toBytes $b partLink @l slide slide slide >";
    private static final int LF = 10;
    private static final int CR = 13;

    public static StringBuffer fromClip() throws Exception {
        return new StringBuffer(Utils.getClipString());
    }

    public static StringBuffer fromBytes(final String o, final String enc) throws Exception {
        final StringBuffer res = new StringBuffer();
        if (enc.length() != 0) {
            final byte[] bb = new byte[o.length()];
            for (int i = 0; i < bb.length; i++)
                bb[i] = (byte) o.charAt(i);
            res.append(new String(bb, enc));
        } else
            Processing.appendCleanBytes(res, o);
        return res;
    }

    private static void appendCleanBytes(final StringBuffer res, final String o) {
        final char[] oo = o.toCharArray();
        final int length = oo.length;
        for (int i = 0; i < length; i++)
            res.append((char) (0xff & oo[i]));
    }

    public static StringBuffer fromBytes(final byte[] o, final String enc) throws Exception {
        if (enc.length() != 0)
            return new StringBuffer(new String(o, enc));
        final StringBuffer res = new StringBuffer();
        final int length = o.length;
        for (int i = 0; i < length; i++)
            res.append((char) (0xff & o[i]));
        return res;
    }

    public static StringBuffer chr(final int cc) throws Exception {
        return new StringBuffer().append((char) cc);
    }

    /**
     * @param o
     *            Object (to be .toString()-ed) containing URL
     * @return
     * @throws Exception
     */
    public static StringBuffer fromUrl(final String url) throws Exception {
        return Utils.suckThru(new byte[1024], new URL(url).openStream());
    }

    /**
     * @param o
     *            Object (to be .toString()-ed) containing file path and name
     * @return
     * @throws Exception
     */
    public static StringBuffer fromFile(final String o) throws Exception {
        if (o.length() > 0)
            return Processing.fromFile(new File(o));
        final StringBuffer res1 = new StringBuffer();
        int n;
        final InputStream is = System.in;
        while (((n = is.read()) >= 0) && (n != Processing.LF) && (n != Processing.CR))
            res1.append((char) (0xFF & n));
        return res1;
    }

    public static StringBuffer fromFile(final File file) throws IOException, FileNotFoundException {
        final FileInputStream is = new FileInputStream(file);
        try {
            return Processing.suckThru(file, is);
        } finally {
            is.close();
        }
    }

    public static Object fromFileo(final String o) throws FileNotFoundException, IOException, ClassNotFoundException {
        return Processing.fromFileo(new File(o));
    }

    public static Object fromFileo(final File file) throws IOException, FileNotFoundException, ClassNotFoundException {
        final FileInputStream is = new FileInputStream(file);
        try {
            return new ObjectInputStream(is).readObject();
        } finally {
            is.close();
        }
    }

    private static StringBuffer suckThru(final File file, final InputStream is) throws IOException {
        return Utils.suckThru(new byte[file.length() > 5000 ? 1024 : 16], is);
    }

    public static void toClip(final String o) throws Exception {
        Utils.getClipboard().setContents(new StringSelection(o), null);
    }

    public static void toFile(final CharSequence what, final String fileName) throws Exception {
        Processing.toFile(what, fileName, false);
    }

    public static void appendToFile(final CharSequence what, final String fileName) throws Exception {
        Processing.toFile(what, fileName, true);
    }

    private static void toFile(final CharSequence what, final String fileName, final boolean append)
            throws FileNotFoundException, IOException {
        final byte[] bb = new byte[1024];
        final OutputStream os = Processing.toFileOpen(fileName, append);
        final int l = what.length();
        for (int i = 0; i < l; i += 1024) {
            final int k = l - i > 1024 ? 1024 : l - i;
            for (int j = 0; j < k; j++)
                bb[j] = (byte) what.charAt(j + i);
            os.write(bb, 0, k);
        }
        Processing.toFileClose(fileName, os);
    }

    public static void toFile(final byte[] what, final String fileName) throws Exception {
        Processing.toFile(what, fileName, false);
    }

    public static void toFileo(final Object what, final String fileName) throws Exception {
        final OutputStream os = Processing.toFileOpen(fileName, false);
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
        objectOutputStream.writeObject(what);
        objectOutputStream.flush();
        Processing.toFileClose(fileName, os);
    }

    public static void appendToFile(final byte[] what, final String fileName) throws Exception {
        Processing.toFile(what, fileName, true);
    }

    private static void toFile(final byte[] what, final String fileName, final boolean append)
            throws FileNotFoundException, IOException {
        final OutputStream os = Processing.toFileOpen(fileName, append);
        final int l = what.length;
        for (int i = 0; i < l; i += 1024) {
            final int k = l - i > 1024 ? 1024 : l - i;
            os.write(what, i, k);
        }
        Processing.toFileClose(fileName, os);
    }

    private static void toFileClose(final String fileName, final OutputStream os) throws IOException {
        os.flush();
        if (!(fileName.length() == 0))
            os.close();
    }

    private static OutputStream toFileOpen(final String fileName, final boolean append) throws FileNotFoundException {
        return fileName.length() == 0 ? (OutputStream) System.out : (OutputStream) new FileOutputStream(fileName,
                append);
    }

    public static StringBuffer toBytes(final String o, final String enc) throws Exception {
        final StringBuffer res = new StringBuffer();
        if (enc.length() != 0) {
            final byte[] bb = o.getBytes(enc);
            for (int i = 0; i < bb.length; i++)
                res.append((char) (255 & bb[i]));
        } else
            Processing.appendCleanBytes(res, o);
        return res;
    }

    /**
     * @param of
     * @param sample
     * @param index
     *            Details: negative if from end, if 0 result is undefined
     * @param switcher
     *            switches which portion to return: 0 - b4!incl , 1 - b4incl , 2
     *            - after incl , 3 - after!incl
     * @return
     */
    // public static CharSequence part(final Object of, final Object sample,
    // final int index, final int switcher) {
    public static CharSequence part(final CharSequence of, final String sample, final int index, final int switcher) {
        final int pos = Utils.nthIndexOf(of, sample, index);
        final int length = of.length();
        switch (switcher) {
        case 0:
            return of.subSequence(0, pos < 0 ? 0 : pos);
        case 1:
            return of.subSequence(0, pos + sample.length());
        case 2:
            return of.subSequence(pos < 0 ? 0 : pos, length);
        default:
            return of.subSequence(pos < 0 ? 0 : (pos + sample.length()), length);
        }
    }

    public static Link partLink(final CharSequence of, final String sample, final int index, final int switcher) {
        final int pos = Utils.nthIndexOf(of, sample, index);
        final int length = of.length();
        final int rpos = pos < 0 ? 0 : pos;
        switch (switcher) {
        case 0:
            return new FinalLink(of.subSequence(0, rpos), new FinalLink(of.subSequence(pos
                    + sample.length(), length), null));
        case 1:
            return new FinalLink(of.subSequence(0, pos + sample.length()), new FinalLink(of.subSequence(pos
                    + sample.length(), length), null));

            // of.subSequence(0, pos + sample.length());
        case 2:
            return new FinalLink(of.subSequence(0, rpos), new FinalLink(of.subSequence(rpos,
                    length), null));
            // of.subSequence(pos < 0 ? 0 : pos, length);
        case -1:
            return Utils.splitLink(of.toString(), sample, pos);

        default:
            return new FinalLink(of.subSequence(0, rpos), new FinalLink(of.subSequence(rpos,
                    pos < 0 ? 0 : (pos + sample.length())), new FinalLink(of.subSequence(pos < 0 ? 0 : (pos + sample
                    .length()), length), null)))
            // of.subSequence(pos + sample.length(), length)
            ;
        }
    }

    public static StringBuffer replace(final StringBuffer where, final String what, final String with, final int mode) {
        final int length = what.length(), length1 = with.length();
        switch (mode) {
        case -1:
            // last
            return Processing.condReplace(where, with, where.lastIndexOf(what), length);
        case 1:
            // first
            return Processing.condReplace(where, with, where.indexOf(what), length);
        case 0:
            // all
            int pos = where.indexOf(what);
            // System.out.println("pos=" + pos);
            while (pos >= 0) {
                where.replace(pos, pos + length, with);
                pos = where.indexOf(what, pos + length1);
                // System.out.println("pos=" + pos);
            }
            return where;
        default:
            // all aggressively from start (mode > 0 ) or from end (mode < 0)
            return mode > 0 ? Processing.replaceAllAggressivelyFromStart(where, what, with) : Processing
                    .replaceAllAggressivelyFromFinish(where, what, with);
        }
    }

    private static StringBuffer condReplace(final StringBuffer where, final String with, final int pos, final int length) {
        return pos < 0 ? where : where.replace(pos, pos + length, with);
    }

    private static StringBuffer replaceAllAggressivelyFromFinish(final StringBuffer where, final String what,
            final String with) {
        int pos = where.lastIndexOf(what);
        while (pos >= 0) {
            where.replace(pos, pos + what.length(), with);
            pos = where.lastIndexOf(what);
        }
        return where;
    }

    private static StringBuffer replaceAllAggressivelyFromStart(final StringBuffer where, final String what,
            final String with) {
        int pos = where.indexOf(what);
        while (pos >= 0) {
            where.replace(pos, pos + what.length(), with);
            pos = where.indexOf(what);
        }
        return where;
    }

    /**
     *
     */
    public static Link copy(final Link link) {
        return Utils.lazy(link);
    }

    public static void pump(final InputStream is, final OutputStream os) {
        Utils.pump(is, os);
    }

    public static Link slide(final Collection c) {
        final Iterator iterator = c.iterator();
        final IteratorAdapter iteratorAdapter = new IteratorAdapter(iterator);
        if (!iterator.hasNext())
            return null;
        final SimpleLinkFactory simpleLinkFactory = new SimpleLinkFactory(iteratorAdapter, IteratorStrategy.INSTANCE);
        return simpleLinkFactory.instance();
    }

    public static Link slide(final java.util.Enumeration e) {
        final NextContentProvider nextContentProvider = new NextContentProvider() {

            private static final long serialVersionUID = 1L;

            public Object tryNextContent() {
                if (!e.hasMoreElements())
                    return null;
                else
                    return Links.nullCorrect(e.nextElement());
            }
        };
        return Links.nextContentProviderBased(nextContentProvider);
    }

    public static Link slide(final Map m) {
        return m != null ? Processing.slide(m.entrySet()) : null;
    }

    public static Link slide(final BackAdapter backAdapter, final BackUsageStrategy strategy) {
        return Utils.backAdapterBasedLink(backAdapter, strategy);
    }

    public static Link slide(final Object[] arr) {
        return Processing.slide(Arrays.asList(arr));
    }

    public static String join(final Link link, final VariablesStorage vs) {
        final StringBuffer sb = new StringBuffer();
        Processing.join(sb, link, vs);
        return sb.toString();
    }

    public static String join(final Link link, final String comma, final VariablesStorage vs) {
        final StringBuffer sb = new StringBuffer();
        final NextContentProvider ncp = new NextContentProvider() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            private Link current = new FinalLink(null, Links.flat(link));
            private boolean odd = true;

            public Object tryNextContent() {
                if (odd)
                    current = current.next();
                final Object nextContent = (current == null) ? null : odd ? comma : Links
                        .nullCorrect(current.content());
                odd = !odd;
                return nextContent;
            }
        };
        final Link link2 = Links.nextContentProviderBased(ncp);
        if (link2 != null)
            Processing.join(sb, link2.next(), vs);
        return sb.toString();
    }

    private static void join(final StringBuffer sb, final Link link, final VariablesStorage vs) {
        Processing.tryJoin(link, sb, vs);
    }

    private static boolean tryJoin(final Link link, final StringBuffer sb, final VariablesStorage vs) {
        final LinkSlider sl = new LinkSlider(link);
        final int length = sb.length();
        while (sl.tryNext()) {
            final Object content = sl.content();
            if (content != null)
                if ((content instanceof Link)) {
                    if (!Processing.tryJoin((Link) content, sb, vs))
                        return Processing.resetToFalse(sb, length);
                } else if (content instanceof Getter) {
                    Utils.debug(null, "Processing.tryJoin():Getter:", content);
                    final Object obj = vs.get(((Getter) content).varname());
                    Utils.debug(null, "Processing.tryJoin():got:", obj);
                    if (obj == null)
                        return Processing.resetToFalse(sb, length);
                    else if (!(obj instanceof Link))
                        sb.append(obj);
                    else if (!Processing.tryJoin((Link) content, sb, vs))
                        return Processing.resetToFalse(sb, length);
                } else if (content instanceof Direct)
                    sb.append(((Direct) content).getContent());
                else
                    sb.append(content);
        }
        return true;
    }

    private static boolean resetToFalse(final StringBuffer sb, final int length) {
        sb.setLength(length);
        return false;
    }

    public static Link loop(final Link link) {
        final NextContentProvider ncp = new NextContentProvider() {
            private static final long serialVersionUID = 1L;
            Link curr = new FinalLink(null, link);

            public Object tryNextContent() {
                final Link next = curr.next();
                return Links.nullCorrect((curr = (next == null) ? link : next).content());
            }
        };
        ;
        ;
        return Links.nextContentProviderBased(ncp);
    }

    public static List as_list(final char[] array) {
        return Processing.wrap(array);
    }

    public static List as_list(final byte[] array) {
        return Processing.wrap(array);
    }

    private static List wrap(final Object arr) {
        return new AbstractList() {

            public int size() {
                return Array.getLength(arr);
            }

            public Object get(final int index) {
                return Array.get(arr, index);
            }

            public Object set(final int index, final Object element) {
                try {
                    return get(index);
                } finally {
                    Array.set(arr, index, element);
                }
            }
        };
    }

    public static List as_list(Link link) {
        final ArrayList res = new ArrayList();
        for (; link != null; link = link.next())
            res.add(link.content());
        return res;
    }

    public static List as_linked_list(Link link) {
        final List res = new LinkedList();
        for (; link != null; link = link.next())
            res.add(link.content());
        return res;
    }

    public static Link gotted(final Link link, final DataStack d) {
        return Processing.gotted(link, d.sub(0));
    }

    public static Link gotted(final Link link, final Map m) {
        final Link res1 = Processing.revNP(link, m, null);
        return Processing.reverse(res1);
    }

    private static Link revNP(final Link link, final Map m, Link res) {
        for (Link base = link; base != null; base = base.next())
            res = new FinalLink(Processing.prcess(base.content(), m), res);
        return res;
    }

    public static Link reverse(final Link link) {
        Link res = null;
        for (Link base = link; base != null; base = base.next())
            res = new FinalLink(base.content(), res);
        return res;
    }

    private static Object prcess(final Object content, final Map m) {
        if ((content instanceof Getter)) {
            final String varname = ((Getter) (content)).varname();
            final Object object = m.get(varname);
            return (object != null) || m.containsKey(varname) ? object : content;
        } else if ((content instanceof Link))
            return Processing.gotted((Link) content, m);
        else
            return content;
    }
}
