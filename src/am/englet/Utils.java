/**
 *
 */
package am.englet;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import am.englet.Links.ValueConverter;
import am.englet.MethodsStorage.Getter;
import am.englet.cast.ClassPool;
import am.englet.cast.ClassPool.classLoader;
import am.englet.compoundncp.SourcedExhaustableNextContentProvider;
import am.englet.compoundncp.SpecialityAwareNextContentProviderProxy;
import am.englet.link.BackAdapter;
import am.englet.link.BackUsageStrategy;
import am.englet.link.FinalLink;
import am.englet.link.LazyLink;
import am.englet.link.Link;
import am.englet.link.NextItemProvider;
import am.englet.link.SimpleLinkFactory;
import am.englet.link.backadapters.ResultSetStrategy;
import am.englet.link.backadapters.slider.FilterSlider;
import am.englet.link.backadapters.slider.LinkSlider;
import am.englet.link.backadapters.slider.LinkSliderAdapter;
import am.englet.link.backadapters.slider.Slider;
import am.englet.reflect.MemberInvokable;
import am.englet.util.Checker;

/**
 * @author Adm1
 * 
 */
public class Utils {

    private static final class TargetEndStaticInvokable implements Invokable {
        private final Invokable inv;
        private final Class returnType;
        private final Class[] parameterTypes;
        {
        }

        private TargetEndStaticInvokable(final Invokable invokable) {
            inv = invokable;
            returnType = inv.returnType();
            parameterTypes = new Class[inv.parameterTypes().length + 1];
            final Class[] src = inv.parameterTypes();
            parameterTypes[src.length] = inv.targetType();
            System.arraycopy(src, 0, parameterTypes, 0, src.length);
        }

        public Class targetType() {
            return null;
        }

        public Class returnType() {
            return returnType;
        }

        public Class[] parameterTypes() {
            return (Class[]) Utils.copy(parameterTypes);
        }

        public Object invoke(final Object obj, final Object[] args) throws IllegalAccessException,
                IllegalArgumentException, InvocationTargetException, InstantiationException {
            final int length = args.length - 1;
            final Object[] args1 = new Object[length];
            System.arraycopy(args, 0, args1, 0, length);
            return inv.invoke(args[length], args1);
        }

        public String toString() {
            return "static for:" + inv;
        }
    }

    private static final class TargetStartStaticInvokable implements Invokable {
        private final Invokable inv;
        private final Class returnType;
        private final Class[] parameterTypes;
        {
        }

        private TargetStartStaticInvokable(final Invokable invokable) {
            inv = invokable;
            returnType = inv.returnType();
            parameterTypes = new Class[inv.parameterTypes().length + 1];
            parameterTypes[0] = inv.targetType();
            final Class[] src = inv.parameterTypes();
            System.arraycopy(src, 0, parameterTypes, 1, src.length);
        }

        public Class targetType() {
            return null;
        }

        public Class returnType() {
            return returnType;
        }

        public Class[] parameterTypes() {
            return (Class[]) Utils.copy(parameterTypes);
        }

        public Object invoke(final Object obj, final Object[] args) throws IllegalAccessException,
                IllegalArgumentException, InvocationTargetException, InstantiationException {
            final int length = args.length - 1;
            final Object[] args1 = new Object[length];
            System.arraycopy(args, 1, args1, 0, length);
            return inv.invoke(args[0], args1);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "static for:" + inv;
        }
    }

    private static final class CachingProxyList extends AbstractList {
        private final List cache;
        private final int size;
        private final List got;
        private final List list;

        private CachingProxyList(final List cache, final int size, final List got, final List list) {
            this.cache = cache;
            this.size = size;
            this.got = got;
            this.list = list;
        }

        public int size() {
            return size;
        }

        public Object get(final int paramInt) {
            final Boolean object = (Boolean) got.get(paramInt);
            if (Boolean.TRUE.equals(object))
                return cache.get(paramInt);
            final Object object2 = list.get(paramInt);
            cache.set(paramInt, object2);
            got.set(paramInt, Boolean.TRUE);
            return object2;
        }
    }

    private static final class ConvertingList extends AbstractList {
        private final ValueConverter valueConverter;
        private final List list;

        private ConvertingList(final ValueConverter valueConverter, final List list) {
            this.valueConverter = valueConverter;
            this.list = list;
        }

        public int size() {
            return list.size();
        }

        public Object get(final int paramInt) {
            return valueConverter.convert(list.get(paramInt));
        }
    }

    private static final class ComposedValueConverter implements ValueConverter {
        private final ValueConverter ofWhat;
        private final ValueConverter of;
        private static final long serialVersionUID = 1L;

        private ComposedValueConverter(final ValueConverter ofWhat, final ValueConverter of) {
            this.ofWhat = ofWhat;
            this.of = of;
        }

        public Object convert(final Object object) {

            return of.convert(ofWhat.convert(object));
        }
    }

    private static final BigInteger MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE);
    private static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

    private static final BigDecimal MIN_INT_BD = BigDecimal.valueOf(Integer.MIN_VALUE);
    private static final BigDecimal MAX_INT_BD = BigDecimal.valueOf(Integer.MAX_VALUE);

    static String getClipString() throws Exception {
        return Utils.getClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor).toString();
    }

    static Clipboard getClipboard() {
        return java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    static CharSequence toCharSequence(final Object o) {
        return o instanceof CharSequence ? (CharSequence) o : o.toString();
    }

    public static Object[] copy(final Object[] oo) {
        final int length = oo.length;
        final Object dest = Array.newInstance(oo.getClass().getComponentType(), length);
        System.arraycopy(oo, 0, dest, 0, length);
        return (Object[]) dest;
    }

    /**
     * @param bb
     *            buffer bytes array
     * @param is
     *            InputStream
     * @return StringBuffer of ((char) (0xFF & bb[i])) from bytes bb[]
     * @throws IOException
     */
    public static StringBuffer suckThru(final byte[] bb, final InputStream is) throws IOException {
        final StringBuffer res = new StringBuffer();
        int n;
        while ((n = is.read(bb)) >= 0)
            for (int i = 0; i < n; i++)
                res.append((char) (0xFF & bb[i]));
        is.close();
        return res;
    }

    /**
     * @param defaultedIfEmpty
     *            (where)
     * @param what
     * @param index
     *            0-th occurrence of "what" string is always out of "where"
     *            string
     * @return position (0-starting) of "index"-th occurrence of "what" string
     *         within "where" or -length of "what" if no such exist
     */
    public static int nthIndexOf(Object where, final String what, int index) {
        if (!(where instanceof StringBuffer))
            where = where.toString();
        final int length = what.length();
        if (index >= 0) {
            int pos = 0;
            for (int i = 0; i < index; i++) {
                pos = Utils.indexOf(where, what, pos) + length;
                if (pos < 0)
                    return -length;
            }
            return pos - length;
        } else {
            int pos = where instanceof StringBuffer ? ((StringBuffer) where).length() : where.toString().length();
            index = -index;
            for (int i = 0; i < index; i++) {
                Utils.debug(System.out, "1:pos:", new Integer(pos));
                pos = Utils.lastIndexOf(where, what, pos - 1);
                Utils.debug(System.out, "2:pos:", new Integer(pos));
                if (pos < 0)
                    return -length;
            }
            return pos;
        }
    }

    private static int indexOf(final Object where, final String what, final int pos) {
        return where instanceof StringBuffer ? ((StringBuffer) where).indexOf(what, pos) : ((String) where).indexOf(
                what, pos);
    }

    private static int lastIndexOf(final Object where, final String what, final int pos) {
        return where instanceof StringBuffer ? ((StringBuffer) where).lastIndexOf(what, pos) : ((String) where)
                .lastIndexOf(what, pos);
    }

    /**
     * Get (proposed) singleton by its class from the Map
     * 
     * @param map
     *            - Class-to-Object Map of singletons
     * @param keyClass
     * @param valueClass
     *            - class to instantiate value of and put into Map if not found
     * @return
     */
    public static Object getEnsuredValueByClassKey(final Map map, final Class keyClass, final Class valueClass) {
        return Utils.getEnsuredValueByKey(map, keyClass, valueClass);
    }

    public static Object getEnsuredValueByKey(final Map map, final Object key, final Class valueClass) {
        Object result = map.get(key);
        if (result == null) {
            try {
                result = valueClass.newInstance();
            } catch (final InstantiationException e) {
                e.printStackTrace();
                return null;
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
            final Object res1 = result;
            Utils.debug(System.out, new CodeBlock() {

                public Object result() {
                    return new Object[] { "Utils.getEnsuredValueByKey():to put:", res1, " :", valueClass.getName(),
                            " keyed by:", key };
                }
            });
            map.put(key, result);
        }
        return result;
    }

    // public static StringBuffer replace(Object where, Object what, Object
    // with, int mode) {
    // }

    public static Object getFirstMatching(final Object[] oo, final String regex) {
        final Pattern pattern = Pattern.compile(regex);
        for (int i = 0; i < oo.length; i++) {
            final String valueOf = String.valueOf(oo[i]);
            if (pattern.matcher(valueOf).matches() || (valueOf.indexOf(regex) >= 0))
                return oo[i];
        }
        return null;
    }

    public static StringBuffer toStringBuffer(final Object o) {
        if (o instanceof StringBuffer)
            return (StringBuffer) o;
        else
            return new StringBuffer(String.valueOf(o));
    }

    public static Object tryInvocationalCasting(final Object s2, final Class srcClass, final Class dstClass)
            throws IllegalAccessException, InvocationTargetException {
        final Object[] initargs = new Object[] { s2 };
        try {
            return Utils.tryConstructor(initargs, srcClass, dstClass);
        } catch (final Exception e) {
            try {
                return Utils.tryStaticFactory(initargs, srcClass, dstClass);
            } catch (final Exception e1) {
                // e1.printStackTrace();
                return Utils.tryFactory(s2, srcClass, dstClass);
            }
        }
    }

    static Object tryFactory(final Object initarg, final Class srcClass, final Class dstClass)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return Utils.findFactoryInstanceMethod(srcClass, dstClass).invoke(initarg, null);
    }

    private static Method findFactoryInstanceMethod(final Class srcClass, final Class dstClass) {
        final Method m = Utils.lookUpFactoryInstanceMethod(srcClass, dstClass);
        if (m == null)
            throw new IllegalArgumentException("No matching factory method found.");
        return m;
    }

    public static Method lookUpFactoryInstanceMethod(final Class srcClass, final Class dstClass) {
        final Method mm[] = srcClass.getMethods();
        Method m = null;
        for (int i = 0; i < mm.length; i++) {
            m = mm[i];
            if (Modifier.isStatic(m.getModifiers()) || (m.getParameterTypes().length != 0)
                    || !m.getReturnType().equals(dstClass))
                continue;
            // TODO implement!!!
        }
        return m;
    }

    static Object tryStaticFactory(final Object[] initargs, final Class srcClass, final Class dstClass)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return Utils.findStaticFactoryMethod(srcClass, dstClass).invoke(null, initargs);
    }

    private static Method findStaticFactoryMethod(final Class srcClass, final Class dstClass) {
        final Method m2 = Utils.lookUpStaticFactoryMethod(srcClass, dstClass);
        if (m2 == null)
            throw new IllegalArgumentException("No matching static factory method found.");
        else
            return m2;
    }

    public static Method lookUpStaticFactoryMethod(final Class srcClass, final Class dstClass) {
        final Method mm[] = dstClass.getMethods();
        Method m2 = null;
        for (int i = 0; i < mm.length; i++) {
            m2 = mm[i];
            final Class rt[] = m2.getParameterTypes();
            if (Modifier.isStatic(m2.getModifiers()) && (rt.length == 1) && rt[0].equals(srcClass))
                break;
        }
        return m2;
    }

    static Object tryConstructor(final Object[] initargs, final Class srcClass, final Class dstClass)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Constructor con = Utils.lookUpConstructor(srcClass, dstClass);
        return con.newInstance(initargs);
    }

    public static Constructor lookUpConstructor(final Class srcClass, final Class dstClass)
            throws NoSuchMethodException {
        return dstClass.getConstructor(new Class[] { srcClass });
    }

    public static ClassLoader ensuredClassLoader(final Class cls) {
        final ClassLoader classLoader = cls != null ? cls.getClassLoader() : null;
        return classLoader != null ? classLoader : ClassLoader.getSystemClassLoader();

    }

    /**
     * @param name
     * @return
     */
    public static String simpleClassname(final String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public static String toClassNameCase(final String str) {
        if ((str == null) || (str.length() == 0))
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String toFieldNameCase(final String str) {
        if ((str == null) || (str.length() == 0))
            return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    private static void process(final Class cl, final Set s) {
        s.add(cl);
        final Class superclass = cl.getSuperclass();
        if (superclass != null)
            Utils.process(superclass, s);
        final Class[] interfaces = cl.getInterfaces();
        for (int i = 0; i < interfaces.length; i++)
            Utils.process(interfaces[i], s);
    }

    /**
     * @param cls
     * @return
     */
    public static Set assignTargetsSet(final Class cls) {
        final Set res = new HashSet();
        Utils.process(cls, res);
        res.remove(cls);
        res.remove(Object.class);
        return res;
    }

    /**
     * @param pkgName
     * @return
     */
    public static String packageNameToPackagePath(final String pkgName) {
        return pkgName.replaceAll("\\.", "/");
    }

    /**
     * @param name2
     * @param classes2
     * @param classLoader
     * @return
     */
    public static URL tryToGetResource(final String name2, final Class[] classes2, final classLoader cl) {
        URL resource = null;
        // System.out.println("Utils.tryToGetResource():name2:" + name2);
        for (int i = 0; i < classes2.length; i++) {
            final Class class1 = classes2[i];
            resource = Utils.ensuredClassLoader(class1).getResource(name2);
            // System.out.println("Utils.tryToGetResource():classes2[i]:"
            // + classes2[i]);
            // System.out.println("Utils.tryToGetResource():resource:" +
            // resource);
            if (resource != null)
                break;
            resource = cl.getResource(name2);
            if (resource != null)
                break;
        }
        return resource;
    }

    public static Method method(final String mask, final ClassPool classpool) {
        final String[] pars = mask.split("\\.", 2);
        final Class cl = classpool.forName(pars[0]);
        final String regex = pars[1];
        return Utils.method(cl, regex);
    }

    /**
     * @param cl
     * @param regex
     * @return
     */
    public static Method method(final Class cl, final String regex) {
        final $ $ = new $(cl) {

            public boolean check(final Method mtd) {
                final String string = mtd.toString();
                return (string.indexOf(regex) >= 0) || string.matches(regex) || string.matches(".*" + regex + ".*");
            }
        };
        final Method method = $.method();
        return method != null ? method : $.declared.method();
    }

    public static Invokable toStatic(final Invokable invokable, final int mode) {
        return (invokable.targetType() == null) ? invokable : mode == -1 ? (Invokable) new TargetStartStaticInvokable(
                invokable) : (Invokable) new TargetEndStaticInvokable(invokable);
    }

    public static Slider splitSlider(final String of, final String sample, final int pos) {
        final int length = sample.length();
        return new Slider() {

            int p = 0, p1 = pos;
            boolean first = true, wasFirst = true;

            public boolean tryNext() {
                // System.out
                // .println("Processing.splitSlider(...).new Slider() {...}.tryNext()"
                // + p + ">" + p1);
                if (first) {
                    first = false;
                    return true;
                }
                if (wasFirst)
                    wasFirst = false;
                if (p1 < 0)
                    return false;
                p = p1;
                p1 = of.indexOf(sample, p + 1);
                return true;
            }

            public Object content() {
                // System.out
                // .println("Processing.splitSlider(...).new Slider() {...}.content()"
                // + p + ">" + p1 + ">" + length);
                final String string = p1 >= 0 ? of.substring(wasFirst ? p : p + length, p1) : of.substring(wasFirst ? p
                        : p + length);
                // System.out.println(string);
                return string;
            }
        };
    }

    /**
     * @param of
     * @param sample
     * @param pos
     * @return
     */
    public static Link splitLink(final String of, final String sample, final int pos) {
        return pos < 0 ? new FinalLink(of, null) : Utils.sliderBasedLink(Utils.splitSlider(of, sample, pos));
    }

    /**
     * @deprecated
     * @param slider
     * @return
     */
    public static Link sliderBasedLink(final Slider slider) {
        return Utils.backAdapterBasedLink(new LinkSliderAdapter(slider), ResultSetStrategy.INSTANCE);
    }

    /**
     * @deprecated
     * @param backAdapter
     * @param strategy
     * @return
     */
    public static Link backAdapterBasedLink(final BackAdapter backAdapter, final BackUsageStrategy strategy) {
        return new SimpleLinkFactory(backAdapter, strategy).instance();
    }

    /**
     * @param link
     * @return
     */
    public static Link lazy(final Link link) {
        if (link == null)
            return null;

        final LinkSlider linkSlider = new LinkSlider(link);
        final LinkSliderAdapter backAdapter = new LinkSliderAdapter(linkSlider);
        final NextItemProvider nextItemProvider = new NextItemProvider(ResultSetStrategy.INSTANCE, backAdapter);
        final Link instance = new LazyLink(nextItemProvider, null);
        // new SimpleLinkFactory(backAdapter,
        // ResultSetStrategy.INSTANCE).instance();
        Utils.debug(System.out, "Processing.copy(): instance = ", instance);
        return instance.next();
    }

    /**
     * @param defaultedIfEmpty
     *            (content)
     * @return
     */
    public static boolean isLink(final Object content) {
        return (content == null) || (content instanceof Link);
    }

    public static Link filterLink(final Link base, final Checker checker) {
        return Utils.sliderBasedLink(new FilterSlider(new LinkSlider(base), checker));
    }

    public static Class deprimitivized(final Class cls) {
        if ((cls == null) || !cls.isPrimitive())
            return cls;
        final Class class1 = (Class) $.DEPRIMITIIVISATORS.get(cls);
        return cls.isPrimitive() && (class1 != null) ? class1 : cls;
    }

    /**
     * @param lookUpField
     * @return
     */
    public static boolean isStatic(final Member lookUpField) {
        return Modifier.isStatic(lookUpField.getModifiers());
    }

    /**
     * @param cl
     * @return
     */
    public static boolean isPublic(final Class cl) {
        return Modifier.isPublic(cl.getModifiers());
    }

    /**
     * Circular addressing implementation
     * 
     * @param list
     * @param pos
     * @return
     */
    public static Object atStack(final List list, final int pos) {
        return list.get(Utils.stackIndex(list, pos));
    }

    /**
     * @param list
     * @param pos
     * @return
     */
    public static int stackIndex(final List list, final int pos) {
        return pos < 0 ? -1 - pos : list.size() - 1 - pos;
    }

    public static boolean toBoolean(final Object o) {
        if (o instanceof Boolean)
            return ((Boolean) o).booleanValue();
        if (o instanceof Number)
            return ((Number) o).intValue() != 0;
        final String s = o.toString();
        if (s.length() == 1) {
            final char c = s.charAt(0);
            return (c != '0') && (c != '-') && (c != 'n') && (c != 'N');
        }
        return (s.length() > 0) && !s.equalsIgnoreCase("false");

    }

    /**
     * @param englet
     * @param defaultedIfEmpty
     *            (object)
     * @param l
     * @return
     * @throws Throwable
     */
    public static Object run(final Englet englet, final Object object, final Link l) throws Throwable {
        final DataStack stack = englet.getStack();
        stack.push(object);
        // 0 8 for {_@*}/ -> 0 || 1 || 4 || 9 || 16 || 25 || 36 || 49 || 64
        stack.put("_", object);
        Management.excl(l, englet.getRstack());
        englet.run();
        if ((stack.size() == 0) || (
        // TODO new
                /* (englet.getStack().size() == 1) && */
                stack.stack().empty()))
            return Utils.STACK_IS_EMPTY;
        final Object pop = stack.pop();
        // TODO new
        stack.stack().clear();
        stack.deframe();
        // TODO /new
        // englet.getStack().deframeAll();
        // TODO Auto-generated method stub
        return pop;
    }

    public static boolean isUpCastable(final Class to, final Class from) {
        final Comparable tor = (Comparable) Utils.NUMBER_CAST_RATES.get(to);
        if (tor == null)
            return false;
        final Comparable fromr = (Comparable) Utils.NUMBER_CAST_RATES.get(from);
        return (fromr != null) && (tor.compareTo(fromr) >= 0);
    }

    private final static Map NUMBER_CAST_RATES;
    public static final Object STACK_IS_EMPTY = new Object();

    private static void put(final Map m, final Class cl, final int rate) {
        final Object c = new Integer(rate);
        m.put(cl, c);
        final Class deprimitivized = Utils.deprimitivized(cl);
        if (deprimitivized != null)
            m.put(deprimitivized, c);
    }

    static {
        final HashMap m = new HashMap();
        final Class[] nnc = new Class[] { boolean.class, byte.class, short.class, char.class, int.class, long.class,
                BigInteger.class, float.class, double.class, BigDecimal.class };
        for (int i = 0; i < nnc.length; i++)
            Utils.put(m, nnc[i], i);
        NUMBER_CAST_RATES = Collections.unmodifiableMap(m);
    }

    /**
     * @param to
     * @param from
     * @return
     */
    public static boolean isCastable(final Class to, final Class from) {
        return to.isAssignableFrom(from) || Utils.deprimitivized(to).isAssignableFrom(from)
                || Utils.isUpCastable(to, from) || Utils.isUpCastable(Utils.deprimitivized(to), from)
                || (to.equals(String.class) && CharSequence.class.isAssignableFrom(from));
    }

    public static Object constantProxy(final VariablesStorage vs, final Class ifc) {
        final Map m1 = Utils.methodsNames(ifc);
        return Utils.constantProxy(vs, ifc, m1);
    }

    public static Invokable constantProxyInvokable(final VariablesStorage vs, final Class ifc, final CastingContext cc) {
        final Object[] srcs = new Object[] { vs, ifc, Utils.methodsNames(ifc) };
        final Method method = new $(Utils.class, Method.class).method(Modifier.STATIC, -1, new Class[] {
                VariablesStorage.class, Class.class, Map.class }, new String[] { "constantProxy" });
        final Invokable inv = new MethodInvokable(method);
        return CompoundInvokable.create(new InvokableDescription(inv, srcs), -1, cc);
    }

    private static Map methodsNames(final Class ifc) throws SecurityException {
        final Method[] methods = ifc.getMethods();
        final HashMap m1 = new HashMap();
        for (int i = 0; i < methods.length; i++)
            m1.put(methods[i], Utils.names(methods[i].getName(), methods[i].getReturnType()));
        return Collections.unmodifiableMap(m1);
    }

    public static Object constantProxy(final VariablesStorage vs, final Class ifc, final Map m1)
            throws IllegalArgumentException {
        final HashMap m0 = new HashMap();
        for (final Iterator i = m1.entrySet().iterator(); i.hasNext();) {
            final Entry next = (Entry) i.next();
            final Method method = (Method) next.getKey();
            m0.put(method, Utils.find(vs, method.getParameterTypes().length, method.getReturnType(), ((List) next
                    .getValue())));
        }
        final ClassLoader classLoader = ifc.getClassLoader();
        final Map m = Collections.unmodifiableMap(m0);
        return Proxy.newProxyInstance(classLoader != null ? classLoader : ClassLoader.getSystemClassLoader(),
                new Class[] { ifc }, new InvocationHandler() {
                    private Method hashCodeMethod;
                    private Method equalsMethod;
                    private Method toStringMethod;
                    {
                        try {
                            hashCodeMethod = Object.class.getMethod("hashCode", null);
                            equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
                            toStringMethod = Object.class.getMethod("toString", null);
                        } catch (final NoSuchMethodException e) {
                            throw new NoSuchMethodError(e.getMessage());
                        }
                    }

                    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                        if (method.equals(toStringMethod))
                            return ifc.getName()
                                    + (proxy != null ? (proxy.getClass().getName() + '@' + Integer.toHexString(proxy
                                            .hashCode())) : "") + m.toString();
                        else if (method.equals(equalsMethod))
                            return (args != null) && (args.length == 1) && (args[0] == proxy) ? Boolean.TRUE
                                    : Boolean.FALSE;
                        else if (method.equals(hashCodeMethod))
                            return new Integer(System.identityHashCode(proxy));
                        else
                            return getReturnValue(m, method, args);
                    }

                    private Object getReturnValue(final Map m, final Method method, final Object[] args)
                            throws Throwable {
                        final Object returnValue = m.get(method);
                        if (returnValue.getClass().equals(Method.class))
                            return ((Method) returnValue).invoke(this, args);
                        else if (returnValue instanceof Invokable)
                            return ((Invokable) returnValue).invoke(this, args);
                        else
                            return returnValue;
                    }
                });
    }

    private static List names(final String name, final Class class1) {
        final List names = new ArrayList();
        Utils.addClassNames(name, names);
        Utils.add(name, names, "get");
        Utils.add(name, names, "is");
        final String simpleName = Utils.simpleName(class1);
        Utils.addClassNames(simpleName, names);
        final char charAt = simpleName.charAt(0);
        if (Character.isUpperCase(charAt))
            names.add(new String(new char[] { Character.toLowerCase(charAt) }) + simpleName.substring(1));
        return Collections.unmodifiableList(names);
    }

    private static void addClassNames(final String name, final List names) {
        names.add(name);
        final String camelCaseToUnderscored = Utils.camelCaseToUnderscored(name);
        if (!camelCaseToUnderscored.equals(name))
            names.add(camelCaseToUnderscored);
    }

    public static String simpleName(final Class class1) {
        return Utils.simpleClassname(class1.getName());
    }

    private static Object find(final VariablesStorage vs, final int parsCount, final Class retType, final List names) {
        for (final Iterator iterator = names.iterator(); iterator.hasNext();) {
            final String str = (String) iterator.next();
            final Object obj = Utils.find(vs, retType, str, parsCount);
            Utils.debug(System.out, "Utils.find():[", str, "]=", obj);
            if (obj != null)
                // TODO Auto-generated method stub
                return obj;
        }
        // TODO Auto-generated method stub
        return Utils.defaultedIfEmpty(null, retType);
    }

    private static Object find(final VariablesStorage vs, final Class cls, final String str, final int l) {
        final Object object = vs.get(str);
        if ((object != null) && (l > 0)) {
            final Class class1 = object.getClass();
            if (List.class.isAssignableFrom(class1)) {
                final List list = (List) object;
                return (list.size() > l) ? list.get(l) : list;
            }
        }
        return object;
    }

    private static Object defaultedIfEmpty(final Object object, Class cls) {
        return object != null ? object
                : Integer.class.equals(cls = Utils.deprimitivized(cls)) ? (Object) new Integer(0) : Long.class
                        .equals(cls) ? (Object) new Long(0L) : Float.class.equals(cls) ? (Object) new Float(0)
                        : Double.class.equals(cls) ? (Object) new Double(0) : String.class.equals(cls) ? (Object) ""
                                : Boolean.class.equals(cls) ? (Object) Boolean.FALSE
                                        : List.class.equals(cls) ? (Object) Collections.EMPTY_LIST : Map.class
                                                .equals(cls) ? (Object) Collections.EMPTY_MAP : null;
    }

    private static void add(final String name, final List names, final String prefix) {
        final int length = prefix.length();
        if (name.startsWith(prefix) && (name.length() > length)) {
            names.add(name.substring(length));
            final String o = name.substring(length, length + 1).toLowerCase() + name.substring(length + 1);
            Utils.addClassNames(o, names);
        }
    }

    private static String camelCaseToUnderscored(final String string) {
        final StringBuffer buffer = new StringBuffer();
        final int length = string.length();
        boolean same = true;
        for (int i = 0; i < length; i++) {
            final char c = string.charAt(i);
            final boolean upperCase = Character.isUpperCase(c);
            if (upperCase && same)
                same = false;
            (upperCase && (i > 0) ? buffer.append('_') : buffer).append(upperCase ? Character.toLowerCase(c) : c);
        }
        return same ? string : buffer.toString();
    }

    public static void pump(final InputStream is, final OutputStream os) {
        final byte[] tmp = new byte[1024];
        boolean flag = false;
        int i = 0;
        while (true) {
            try {
                // System.out.println("Utils.pump():flag:" + flag);
                if ((is.available() > 0) || flag)
                    while ((is.available() > 0) || flag) {
                        flag = false;
                        // System.out.println("Utils.pump():i:" + i);
                        i = is.read(tmp, 0, 1024);
                        if (i <= 0)
                            break;
                        os.write(tmp, 0, i);
                    }
                else
                    flag = true;
            } catch (final IOException e) {
                e.printStackTrace();
            }
            if (i < 0)
                break;
            try {
                Thread.sleep(1000);
            } catch (final Exception ee) {
            }
        }
    }

    public static Link parameterized(final Link link, final DataStack ds) {
        final ValueConverter vc = new ValueConverter() {

            private static final long serialVersionUID = -7921627508663021828L;

            public Object convert(final Object object) {
                final DataStack ds2 = ds;
                return object == null ? null : object instanceof String ? object : object instanceof Getter ? ds2
                        .get(((Getter) object).varname()) : object instanceof Link ? Utils.parameterized((Link) object,
                        ds2) : object;
            }
        };
        return Links.valueConverterBased(link, vc);
    }

    // am.englet.Links.recursiveAtomValueConverterBased(Link, ValueConverter)
    // meth di
    public static Link formatLink(final Link link, final DataStack ds) {
        return Links.valueConverterBased(link, new ValueConverter() {

            private static final long serialVersionUID = -8273834821183477291L;

            public Object convert(final Object object) {
                return object == null ? null : object instanceof String ? object : object instanceof Getter ? ds
                        .get(((Getter) object).varname()) : object instanceof Link ? Utils
                        .formatLink((Link) object, ds) : object instanceof Number ? ds.top().st.get(((Number) object)
                        .intValue()) : object;
            }
        });
    }

    public static Link curryLink(final Link link, final Map m) {
        return am.englet.Links.recursiveAtomValueConverterBased(link, new ValueConverter() {

            private static final long serialVersionUID = -2653807948194204612L;

            public Object convert(final Object object) {
                if (!(object instanceof Getter))
                    return object;
                final String varname = ((Getter) object).varname();
                final Object object2 = m.get(varname);
                return (object2 != null) ? object2 : m.containsKey(varname) ? null : object;
            }
        });
    }

    public static void outPrintln(final PrintStream printStream, final String x) {
        printStream.println(x);
    }

    public static void debug(final PrintStream printStream, final Object[] oo) {
        if (Englet.debug) {
            Utils.dump(printStream, oo);
            printStream.println();
        }
    }

    private static void dump(final PrintStream printStream, final Object[] oo) {
        final PrintStream printStream2 = Utils.printStream(printStream);
        if (oo != null)
            for (int i = 0; i < oo.length; i++)
                printStream2.print(oo[i]);
    }

    public static Object debugged(final PrintStream printStream, final Object[] oo, final Object o, final Object[] after) {
        if (Englet.debug) {
            Utils.dump(printStream, oo);
            Utils.dump(printStream, new Object[] { o });
            Utils.dump(printStream, after);
            Utils.printStream(printStream).println();
        }
        return o;
    }

    public static void debug(final PrintStream printStream, final CodeBlock b) {
        if (Englet.debug) {
            final PrintStream printStream2 = Utils.printStream(printStream);
            final Object result = b.result();
            Utils.print(printStream2, result instanceof Collection ? ((Collection) result).toArray() : result);
            printStream2.println();
        }
    }

    private static PrintStream printStream(final PrintStream printStream) {
        return printStream != null ? printStream : System.out;
    }

    public static void debug(PrintStream printStream, final Object o1, final Object o2) {
        if (Englet.debug) {
            if (printStream == null)
                printStream = System.out;
            printStream.print(o1);
            printStream.print(o2);
            printStream.println();
        }
    }

    public static void debug(PrintStream printStream, final Object o1, final Object o2, final Object o3) {
        if (Englet.debug) {
            if (printStream == null)
                printStream = System.out;
            printStream.print(o1);
            printStream.print(o2);
            printStream.print(o3);
            printStream.println();
        }
    }

    public static void debug(PrintStream printStream, final Object o1, final Object o2, final Object o3, final Object o4) {
        if (Englet.debug) {
            if (printStream == null)
                printStream = System.out;
            printStream.print(o1);
            printStream.print(o2);
            printStream.print(o3);
            Utils.print(printStream, o4);
            printStream.println();
        }
    }

    public static void debug(PrintStream printStream, final Object o1, final Object o2, final Object o3,
            final Object o4, final Object o5) {
        if (Englet.debug) {
            if (printStream == null)
                printStream = System.out;
            printStream.print(o1);
            printStream.print(o2);
            printStream.print(o3);
            printStream.print(o4);
            Utils.print(printStream, o5);
            printStream.println();
        }
    }

    public static void debug(PrintStream printStream, final Object o1, final Object o2, final Object o3,
            final Object o4, final Object o5, final Object o6) {
        if (Englet.debug) {
            if (printStream == null)
                printStream = System.out;
            printStream.print(o1);
            printStream.print(o2);
            printStream.print(o3);
            printStream.print(o4);
            Utils.print(printStream, o5);
            Utils.print(printStream, o6);
            printStream.println();
        }
    }

    private static void print(final PrintStream printStream, final Object o4) throws IllegalArgumentException,
            ArrayIndexOutOfBoundsException {
        if ((o4 != null) && o4.getClass().isArray()) {
            final int length = Array.getLength(o4);
            for (int i = 0; i < length; i++)
                printStream.print(Array.get(o4, i));
        } else
            printStream.print(o4);
    }

    public static void debug(PrintStream printStream, final Throwable t, final Object o1, final Object o2,
            final Object o3) {
        if (Englet.debug) {
            if (printStream == null)
                printStream = System.out;
            printStream.print(o1);
            printStream.print(o2);
            printStream.print(o3);
            printStream.println();
            t.printStackTrace();
        }
    }

    public static void debug(PrintStream printStream, final Throwable t, final Object o1, final Object o2,
            final Object o3, final Object o4) {
        if (Englet.debug) {
            if (printStream == null)
                printStream = System.out;
            printStream.print(o1);
            printStream.print(o2);
            printStream.print(o3);
            printStream.print(o4);
            printStream.println();
            t.printStackTrace();
        }
    }

    public static void debug(PrintStream printStream, final Throwable t, final Object o1, final Object o2,
            final Object o3, final Object o4, final Object o5) {
        if (Englet.debug) {
            if (printStream == null)
                printStream = System.out;
            printStream.print(o1);
            printStream.print(o2);
            printStream.print(o3);
            printStream.print(o4);
            printStream.print(o5);
            printStream.println();
            t.printStackTrace();
        }
    }

    public static void debug(PrintStream printStream, final Throwable t, final Object o1, final Object o2) {
        if (Englet.debug) {
            if (printStream == null)
                printStream = System.out;
            printStream.print(o1);
            printStream.print(o2);
            printStream.println();
            t.printStackTrace();
        }
    }

    public static Object correctValue(final Object val0) {
        if (!(val0 instanceof Number))
            return val0;
        if (val0 instanceof Long) {
            final long longValue = ((Long) val0).longValue();
            return (longValue > Integer.MAX_VALUE) || (longValue < Integer.MIN_VALUE) ? val0 : new Integer(
                    ((Long) val0).intValue());
        }
        if (val0 instanceof BigInteger) {
            final BigInteger b = (BigInteger) val0;
            return (b.compareTo(Utils.MAX_INT) > 0) || (b.compareTo(Utils.MIN_INT) < 0) ? val0 : new Integer(b
                    .intValue());
        }
        if (val0 instanceof BigDecimal) {
            final BigDecimal b = (BigDecimal) val0;
            // System.out.println("Utils.correctValue():scale:" + b.scale());
            return (b.scale() > 0) || (b.compareTo(Utils.MAX_INT_BD) > 0) || (b.compareTo(Utils.MIN_INT_BD) < 0) ? val0
                    : new Integer(b.intValue());
        }
        return val0;
    }

    public static Englet deriveEnglet(final DataStack ds, final MethodsStorage m, final ClassPool classPool) {
        return new Englet(ds.derive(), m, classPool);
    }

    public static int paramTypesCompare(final Class[] paramTypes, final Class[] paramTypes2) {
        final int length = paramTypes.length;
        if (length != paramTypes2.length)
            return paramTypes2.length - length;
        else
            for (int i = 0; i < paramTypes2.length; i++) {
                final Class class1 = Utils.deprimitivized(paramTypes[i]);
                // if null then treated as more basic than object <=
                // BSH
                final Class class2 = Utils.deprimitivized(paramTypes2[i]);

                final boolean b = class1 != null;
                if (b != (class2 != null))
                    return b ? -1 : 1;
                if (!b || class1.equals(class2))
                    continue;
                if ($.isMoreGeneralThan(class2, class1))
                    return -1;
                if ($.isMoreGeneralThan(class1, class2))
                    return 1;
            }
        return 0;
    }

    public static Object divLinkContentCandidate(final Object content2, final ValueConverter c) {
        final Object result = c.convert(content2);
        final Object object = Boolean.TRUE.equals(result) ? content2 : result;
        return object;
    }

    public static Links.NextContentProvider oneLevelFlattingValueConverterBasedNextContentProvider(final Link through,
            final Links.ValueConverter valueConverter) {
        final LinkBasedNextContentProvider throughLBNCP = new LinkBasedNextContentProvider(through);
        final ValueConverterBasedNextContentProviderProxy converterProxied = new ValueConverterBasedNextContentProviderProxy(
                throughLBNCP, valueConverter);
        final StackedSENCPBase stackedSENCPBase = new StackedSENCPBase(converterProxied, 1);
        final SourcedExhaustableNextContentProvider sourcedExhaustableNextContentProvider = new SourcedExhaustableNextContentProvider(
                stackedSENCPBase, stackedSENCPBase);
        final SpecialityAwareNextContentProviderProxy specialityAwareNextContentProviderProxy = new SpecialityAwareNextContentProviderProxy(
                sourcedExhaustableNextContentProvider, stackedSENCPBase, stackedSENCPBase);
        return specialityAwareNextContentProviderProxy;
    }

    public static Properties props(final InputStream fileInputStream) throws IOException {
        final Properties pr = new Properties(System.getProperties());
        pr.load(fileInputStream);
        return new Properties(pr);
    }

    public static Properties getPropertiesByResource(final Class class1, final String resource) {
        final Properties p = new Properties();
        try {
            final InputStream s = class1.getResourceAsStream(resource);
            p.load(s);
            s.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    public final static MemberInvokable instantiator(final Class cls, Class[] argClasses) {
        if (argClasses == null)
            argClasses = Invokable.NO_CLASSES;
        final Class deprimitivised = (Class) $.DEPRIMITIIVISATORS.get(cls);
        final Class cls2 = deprimitivised != null ? deprimitivised : cls;
        final Constructor con = new $(cls2, Constructor.class).constructor(argClasses);
        if (con != null)
            return new ConstructorInvokable(con);
        // return new Invokable() {
        // public Object invoke(final Object obj, final Object[] args)
        // throws IllegalAccessException, IllegalArgumentException,
        // InvocationTargetException, InstantiationException {
        // return con.newInstance(args != null ? args : new Object[0]);
        // }
        // };
        final Method method = new $(cls2) {
            public boolean check(final Method mtd) {
                return mtd.getReturnType().equals(cls);
            }
        }.method(Modifier.STATIC, -1, argClasses);
        if (method != null)
            return new MethodInvokable(method);
        // return new Invokable() {
        // public Object invoke(final Object obj, final Object[] args)
        // throws IllegalAccessException, IllegalArgumentException,
        // InvocationTargetException, InstantiationException {
        // return method.invoke(null, args != null ? args : new Object[0]);
        // }
        // };
        if (argClasses.length == 0) {
            final Field field = new $(cls2, Field.class).field(Modifier.STATIC, -1, cls2);
            if (field != null)
                return new FieldGetInvokable(field);
            // return new Invokable() {
            // public Object invoke(final Object obj, final Object[] args)
            // throws IllegalAccessException, IllegalArgumentException,
            // InvocationTargetException, InstantiationException {
            // return field.get(null);
            // }
            // };
        }
        return null;
    }

    public static final Set ARRAY_METHOD_NAMES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] {
            "get", "set", "getLength" })));

    public static boolean checkIfOfArray(final Method member) {
        return Array.class.equals(member.getDeclaringClass()) // &&
                // member.getParameterTypes()[0].isArray()
                && Utils.ARRAY_METHOD_NAMES.contains(member.getName());
    }

    public static List cachingProxy(final List list) {
        if (list == null)
            return null;
        final int size = list.size();
        final List got = Arrays.asList(new Boolean[size]);
        final List cache = Arrays.asList(new Object[size]);
        return new CachingProxyList(cache, size, got, list);
    }

    public static List converting(final List list, final ValueConverter valueConverter) {
        return new ConvertingList(valueConverter, list);
    }

    public static Object firstNotNullIfExists(final Iterator i) {
        while (i.hasNext()) {
            final Object next = i.next();
            if (next != null)
                return next;
        }
        return null;
    }

    public static ValueConverter compose(final ValueConverter ofWhat, final ValueConverter of) {
        return new ComposedValueConverter(ofWhat, of);
    }
}
