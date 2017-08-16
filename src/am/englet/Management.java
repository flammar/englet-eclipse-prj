/**
 *
 */
package am.englet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import am.englet.DataStack.StackFrame;
import am.englet.MethodsStorage.Cast;
import am.englet.MethodsStorage.MethodRecord;
import am.englet.cast.ClassPool;
import am.englet.link.Chain;
import am.englet.link.FinalLink;
import am.englet.link.Link;
import am.englet.link.LinkUtils;
import am.englet.lookup.Lookup;
import am.englet.reflect.MemberInvokable;
import am.englet.util.Checker;

/**
 * @author Adm1
 * 
 */
public class Management {

    public static final String METHOD_NAME_REPLACEMENTS = "excl ! "
            + "IF if printstack ?s frame , frame [ deframe ; IFeq =if dup +x drop -x ntrec @r "
            + "append !t multiPut !l peekVar !x put2 !2 put3 !3 put4 !4 call ! "
            + "nAtOut @s nAtIn !s yOut @y get @v get @ param1 !p dupClone +c startDirect !d "
            + "atX @x atXX @xx dropAll -s asClass .class array array array ] "
            + "processor processor processor proc processor !r compose compose compose !r pure pure pure - "
            + "describe ?d describe describe direct direct direct \\ "
            + "setTopQiuet . lastX ?x lastY ?y lastZ ?z lastT ?t lastU ?u lastV ?v lastW ?w ifgo ?g "
            + "start1 !1 startfs !fs startns !ns if_instead ?i ";
    private static final FinalLink DEFRAMER_FINAL_LINK = new FinalLink(";".intern());

    public static void adapt_immediate_class(final String claszName, final MethodsStorage ms, final ClassPool classPool) {
        Management.adaptClass(claszName, ms, MethodRecord.Type.IMMEDIATE, classPool);
    }

    /**
     * Adapt as management type method method of specified class with specified
     * name that can be treated as management type method
     * 
     * @param clsName
     * @param mtdName
     * @param mtdStor
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public static void adapt_management(final String clsName, final String mtdName, final MethodsStorage mtdStor)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException {
        Management.adaptMethod(mtdStor, MethodRecord.Type.MANAGEMENT, new $(Class.forName(clsName)) {

            public boolean check(final Method mtd) {
                return mtd.getName().equals(mtdName) && Englet.isManagementMethod(mtd);
            }

        }.method(new String[] { mtdName }), mtdName);
    }

    public static void adapt_method(final MethodsStorage ms, final Class clazz, final String regex, final String key) {
        Management.adaptMethod(ms, MethodRecord.Type.PROCESSING, (Method) Utils.getFirstMatching(clazz.getMethods(),
                regex), key);
    }

    public static void adapt_management_class(final String claszName, final MethodsStorage ms, final ClassPool classPool) {
        Management.adaptClass(claszName, ms, MethodRecord.Type.MANAGEMENT, classPool);
    }

    public static void adapt_processing_class(final String claszName, final MethodsStorage ms, final ClassPool classPool) {
        Management.adaptClass(claszName, ms, MethodRecord.Type.PROCESSING, classPool);
    }

    /**
     * Adapt all public methods if to adapt as processing methods
     * 
     * Adapt every public method if to adapt as management or immediate methods
     * or it has at least one argument of "basic" singleton type.
     * 
     * @param clname
     * @param mstor
     * @param type
     * @param classPool
     *            TODO
     */
    public static void adaptClass(final String clname, final MethodsStorage mstor, final int type,
            final ClassPool classPool) {
        // try
        {
            final Class cl = /* classPool != null ? */classPool.forName(clname)
            /* : Class.forName(clname) */;
            final HashMap replacements = Management.getReplacementsMap(cl);
            Utils.debug(System.out, "Management.adaptClass():replacements:", replacements);
            new $(cl) {

                public void each(final Method mtd) {
                    if (Management.toAdapt(type, mtd)) {
                        final StringTokenizer stringTokenizer = new StringTokenizer(Management.replacement(
                                replacements, mtd.getName()).trim());
                        while (stringTokenizer.hasMoreTokens())
                            Management.adaptMethod(mstor, type, mtd, stringTokenizer.nextToken());
                    }
                }
            }.$();
        }
        // catch (final ClassNotFoundException x1) {
        // x1.printStackTrace();
        // }
    }

    private static boolean toAdapt(final int type, final Method m) {
        return Englet.isManagementMethod(m) || (MethodRecord.Type.PROCESSING == type);
    }

    private static String replacement(final HashMap replacements, final String mtdName) {
        final Object replace = replacements.get(mtdName);
        final String key = replace == null ? mtdName : replace.toString();
        return key;
    }

    /**
     * @param cl
     * @return Map of method name replacements to use short method references
     *         instead of long ones, e.g. '*' instead of 'times' or 'multiply'
     */
    private static HashMap getReplacementsMap(final Class cl) {
        final HashMap replacement = new HashMap();
        try {
            final Field ff = cl.getField("METHOD_NAME_REPLACEMENTS");
            final List l = Collections.list(new StringTokenizer(ff.get(null).toString()));
            Utils.debug(System.out, "Management.getReplacementsMap():l:", l);
            for (int i = 0; i < l.size();) {
                final Object key = l.get(i++);
                final Object value = l.get(i++);
                final Object object0 = replacement.get(key);
                if (object0 instanceof StringBuffer)
                    ((StringBuffer) object0).append(" ").append(value);
                else
                    replacement.put(key, object0 == null ? value : new StringBuffer().append(object0).append(" ")
                            .append(value));
            }
        } catch (final Exception x) {
        }
        return replacement;
    }

    public static void deframe(final DataStack st) {
        st.deframe();
    }

    public static void drop(final DataStack st) {
        st.pop();
    }

    public static void dropAll(final DataStack st) {
        st.stack().setSize(0);
    }

    public static void dup(final DataStack st) {
        final Object arg1 = st.pop();
        st.push(arg1);
        st.push(arg1);
    }

    public static String dupClone(final StringBuffer bb, final DataStack st) {
        st.push(bb);
        return bb.toString();
    }

    public static void excl(final Link l, final CommandSource rs) {
        Utils.debug(System.out, "excl:start", "");
        rs.start(l);
    }

    public static Link excl(final Integer i, final CommandSource rs) {
        Utils.debug(System.out, "excl:currrent", "");
        final Link current = rs.current(i.intValue());
        return current;
    }

    public static void excl(final Object o, final String s, final VariablesStorage vs) {
        Utils.debug(System.out, "excl:put", "");
        vs.put(s, o);
    }

    public static void put2(final String s, final DataStack ds) {
        Utils.debug(System.out, "put2", "");
        ds.put(s, ds.peekResultList(2));
    }

    public static void put3(final String s, final DataStack ds) {
        Utils.debug(System.out, "put3", "");
        ds.put(s, ds.peekResultList(3));
    }

    public static void put4(final String s, final DataStack ds) {
        Utils.debug(System.out, "put4", "");
        ds.put(s, ds.peekResultList(4));
    }

    public static void multiPut(final String s, final DataStack ds) {
        Utils.debug(System.out, "multiPut", "");
        ds.put(s, ds.peekResultList());
    }

    public static Object peekVar(final Object o, final String s, final DataStack ds/*
                                                                                    * ,
                                                                                    * final
                                                                                    * CommandSource
                                                                                    * rs
                                                                                    */) {
        Utils.debug(System.out, "peekVar", "");
        ds.put(s, o);
        return o;
    }

    public static void frame(final DataStack st) {
        st.frame();
    }

    public static void go(final Link l, final CommandSource rs/*
                                                               * , final
                                                               * MethodsStorage
                                                               * ms
                                                               */) {
        rs.go(l, 0);
    }

    public static void IF(final DataStack st, final CommandSource rs/*
                                                                     * , final
                                                                     * MethodsStorage
                                                                     * ms
                                                                     */) {
        final Object elsE = st.pop(), then = st.pop();
        final Boolean res = (Boolean) st.pop();
        final Object l = res.booleanValue() ? then : elsE;
        Management.startObject(st, rs, l);
    }

    public static void IFeq(final DataStack st, final CommandSource rs/*
                                                                       * , final
                                                                       * MethodsStorage
                                                                       * ms
                                                                       */) {
        final Object elsE = st.pop(), then = st.pop(), arg2 = st.pop(), arg1 = st.peek();
        final boolean res = (arg1 == null) ? (arg2 == null) : arg1.equals(arg2);
        final Object l = res ? then : elsE;
        if (res)
            st.pop();
        Management.startObject(st, rs, l);
    }

    public static void ngo(final Link l, final int i, final CommandSource rs/*
                                                                             * ,
                                                                             * final
                                                                             * MethodsStorage
                                                                             * ms
                                                                             */) {
        rs.go(l, i);
    }

    public static void ntrec(final int i, final CommandSource rs/*
                                                                 * , final
                                                                 * MethodsStorage
                                                                 * ms
                                                                 */) {
        rs.go(rs.current(i), i);
    }

    // public static void parse(final DataStack st, final MethodsStorage ms)
    // throws Exception {
    // final Englet englet = new Englet(st, ms);
    // final String s = st.pop().toString();
    // Management.parse(s, englet, englet, englet.getParserFactory(), ms);
    // }

    public static void printstack(final DataStack st) {
        Utils.outPrintln(System.out, "printstack:" + st);
    }

    public static void start(final Englet e) throws Throwable {
        Management.start((Link) e.getStack().pop(), e.getRstack());
        e.run();
    }

    public static void startDirect(final Link l, final CommandSource rs) {
        rs.start(Management.direct(l, rs));
    }

    /**
     * @param l
     * @return
     */
    public static Link direct(final Link l, final CommandSource rs) {
        return l != null ? new Link() {

            private Link l1 = l;

            public Link next() {
                l1 = l1.next();
                return l1 != null ? this : null;
            }

            public String toString() {
                return "directificator for: " + l1.toString();
            }

            public Object content() {
                final Object content = l1.content();
                // System.out.println(content.getClass());
                return content instanceof MethodsStorage.Direct ? content : new MethodsStorage.Direct(content);
            }
        } : l;
    }

    public static void start(final Link l, final CommandSource cs) {
        cs.start(l);
    }

    // public static void startf(final Link l, final CommandSource rs,
    // final DataStack ds) {
    // ds.frame();
    // rs.start(l);
    // }

    public static void startfs(final CommandSource cs, final DataStack ds, final Link l) {
        ds.frame();
        ds.top().shading = true;
        cs.start(Management.DEFRAMER_FINAL_LINK);
        cs.start(l);
    }

    public static void startlet(final DataStack st, final MethodsStorage ms, final ClassPool classPool)
            throws Throwable {
        final Englet englet = new Englet(st, ms, classPool);
        Management.start((Link) englet.getStack().pop(), englet.getRstack());
        englet.run();
    }

    private static void startObject(final DataStack st, final CommandSource rs, final Object l) {
        if (l instanceof Link)
            rs.start((Link) l);
        else if (l != null)
            st.push(l);
    }

    public static void parse(final String commands, final ArgumentProvider englet, final ResultHandler handler,
            final ServiceTokenizerFactory parserFactory, final MethodsStorage methods) throws Exception {
        Englet.parse(commands, englet, handler, parserFactory, methods);
    }

    public static void parse_top(final DataStack ds, final ArgumentProvider prov, final ResultHandler handler,
            final TokenizerFactory parserFactory, final MethodsStorage methods, final CommandSource cs)
            throws Exception {
        final StackFrame top = ds.top();
        final ArrayList arrayList = new ArrayList(top.entrySet());
        for (final Iterator i = arrayList.iterator(); i.hasNext();) {
            final Entry e = (Entry) i.next();
            final Object key2 = e.getKey();
            final String key = key2.toString();
            if ((key2 instanceof String) && key.startsWith("{") && key.endsWith("}")) {
                final String key1 = key.substring(1, key.length() - 1);
                final String string = e.getValue().toString();
                final FinalLink chain = Management.parse(ds, prov, handler, parserFactory, methods, string);
                top.put(key1, chain);
                top.remove(key2);
            }
        }
        final Object object = top.get("");
        if ((object != null) && (object instanceof String))
            Management.excl(Management.parse(ds, prov, handler, parserFactory, methods, (String) object), cs);
    }

    /**
     * @param ds
     * @param englet
     * @param handler
     * @param parserFactory
     * @param methods
     * @param string
     * @return
     * @throws Exception
     */
    private static FinalLink parse(final DataStack ds, final ArgumentProvider englet, final ResultHandler handler,
            final TokenizerFactory parserFactory, final MethodsStorage methods, final String string) throws Exception {
        ds.frame();
        Englet.parse(string, englet, handler, parserFactory, methods);
        ds.enlist();
        final List l = (List) ds.pop();
        ds.deframe();
        final FinalLink chain = LinkUtils.ListAsFinalLinkChain(l);
        return chain;
    }

    public static void dump(final MethodsStorage ms) {
        System.err.println(ms);
    }

    public static void append(final Link link, final int n, final CommandSource rs) {
        rs.append(link, n);
    }

    private static void adaptMethod(final MethodsStorage ms, final int methodType, final Method method, final String key) {
        // if (m.getName().equals("excl"))
        // System.out.println("adaptMethod: to put " + m + " as " + key);
        ms.put(key, new MethodRecord(new MethodInvokable /* Metadata */(method), methodType));
    }

    public static void adapt_invokable(final MethodsStorage ms, final Invokable method, final String key) {
        // if (m.getName().equals("excl"))
        // System.out.println("adaptMethod: to put " + m + " as " + key);
        if ((method instanceof ConstructorInvokable) && Management.conClassNameFitsKey(method, key)) {
            final Member member = ((MemberInvokable) method).getMember();
            final Class declaringClass = member.getDeclaringClass();
            final Constructor[] constructors = declaringClass.getConstructors();
            final HashSet hashSet = new HashSet(Arrays.asList(constructors));
            hashSet.remove(member);
            hashSet.add(member);
            for (final Iterator iterator = hashSet.iterator(); iterator.hasNext();)
                Management.do_adapt_invokable(ms, new ConstructorInvokable((Constructor) iterator.next()), key);
        } else if ((method instanceof MethodInvokable)
                && Utils.checkIfOfArray((Method) ((MethodInvokable) method).getMember()))
            for (final Iterator iterator = Arrays.asList(Lookup.ARRAY_COMPONENT_CLASSES).iterator(); iterator.hasNext();) {
                final Class next = (Class) iterator.next();
                final MethodInvokable method2 = new MethodInvokable((Method) ((MethodInvokable) method).getMember(),
                        next);
                Management.do_adapt_invokable(ms,

                (Invokable) Utils.debugged(null, new Object[] { "adapt_invokable():to adapt:" }, method2, new Object[] {
                        " with ", next }), key);
            }
        else
            Management.do_adapt_invokable(ms, method, key);
    }

    private static boolean conClassNameFitsKey(final Invokable method, final String key) {
        final Constructor con = (Constructor) ((MemberInvokable) method).getMember();
        return Utils.simpleName(con.getDeclaringClass()).toLowerCase().equals(key.replaceAll("_", "").toLowerCase());
    }

    private static void do_adapt_invokable(final MethodsStorage ms, final Invokable method, final String key) {
        ms.put(key, new MethodRecord(method, MethodRecord.Type.PROCESSING));
    }

    public static ResultList invokable(final DataStack dataStack, final Object object, final String name) {
        final Invokable lookUpGetterInvokable = Lookup.lookUpGetterInvokable(name, new Class[] { object.getClass() });

        return new ResultList(lookUpGetterInvokable != null ? new Object[] { object, lookUpGetterInvokable }
                : new Object[] { object });
    }

    // TODO compound invokable creation

    // Still need to have at least one of: DataStack.class, CommandSource.class,
    // MethodsStorage.class - among args to be recognize as management method
    public static Invokable invokable(final String clasz, final String mask, final String type,
            final ClassPool classPool) {
        // if (m.getName().equals("excl"))
        // System.out.println("adaptMethod: to put " + m + " as " + key);
        final char c = Character.toLowerCase(type.charAt(0));
        final Class forName = classPool.forName(clasz);
        switch (c) {
        case 'c':
            return new ConstructorInvokable(new $(forName) {

                public boolean check(final Constructor con) {
                    final String string = con.toString();
                    return (string.indexOf(mask) >= 0) || string.matches(mask);
                }
            }.constructor());
        case 'g':
            return new FieldGetInvokable(new $(forName) {

                public boolean check(final Field mmb) {
                    final String string = mmb.toString();
                    return (string.indexOf(mask) >= 0) || string.matches(mask);
                }
            }.field());
        case 's':
            return new FieldSetInvokable(new $(forName) {

                public boolean check(final Field mmb) {
                    final String string = mmb.toString();
                    return (string.indexOf(mask) >= 0) || string.matches(mask);
                }
            }.field());
        default:
            return new MethodInvokable(new $(forName) {

                public boolean check(final Method mmb) {
                    final String string = mmb.toString();
                    return (string.indexOf(mask) >= 0) || string.matches(mask);
                }
            }.method());
        }
    }

    public static Cast as(final Object o, final Object cls, final ClassPool classPool, final CastingContext ctx) {
        final Class tc = !((cls instanceof Class) || (cls == null)) ? classPool.forName(cls.toString()) : (Class) cls;
        return new Cast(o, tc, o != null ? ctx.canCast(tc, o.getClass()) : false);
    }

    public static void call(final Invokable method, final ArgumentProvider argProvider, final ResultHandler handler,
            final CommandSource rs) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
            InstantiationException {
        MethodsStorage.invoke(argProvider, handler, method);
    }

    public static InvokableDescription describe(final Invokable method, final ArgumentProvider argProvider/*
                                                                                                           * ,
                                                                                                           * final
                                                                                                           * CommandSource
                                                                                                           * rs
                                                                                                           */)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // System.out.println("Management.describe(): start");
        final Class[] parameterTypes = method.parameterTypes();
        int length = parameterTypes.length;
        final Class targetType = method.targetType();
        final boolean nonSatic = targetType != null;
        if (nonSatic)
            length++;
        final Class[] argumentClasses = new Class[length];
        // Arguments count must equal but arg classes needed are left null
        // because real arg types are Invokable ancestors but not types returned
        // by those Invokable instances

        // if (b)
        // argumentClasses[0] = targetType;
        // System.arraycopy(parameterTypes, 0, argumentClasses, b ? 1 : 0,
        // parameterTypes.length);

        // if (Englet.debug)
        // System.out.println("Management.describe():argumentClasses:"
        // + Arrays.asList(argumentClasses));

        // ServiceObject om Argument frum {} av Nux nA di
        if (nonSatic && ServiceObject.class.isAssignableFrom(targetType))
            argumentClasses[0] = targetType;
        for (int i = 0, j = nonSatic ? 1 : 0; j < length; i++, j++) {
            final Class cls = parameterTypes[i];
            if ((cls != null) && ServiceObject.class.isAssignableFrom(cls))
                argumentClasses[j] = cls;
        }
        final Object[] arguments = argProvider.getArgumentsAndTarget(argumentClasses, null).arguments();
        return new InvokableDescription(method, arguments);
    }

    public static InvokableDescription same(final ArgumentProvider argProvider) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        final InvokableDescription describe = Management.describe(Invokable.SAME_OBJECT_RETURNING_INVOKABLE,
                argProvider);
        // compose iT m Arg de InvokableDescription I m om Comment om
        return /* new Cast( */describe// ,
        // describe.getInvokable().parameterTypes()[0])
        //
        ;
    }

    public static CompoundInvokable compose(final InvokableDescription description, final DataStack stack,
            final ArgumentProvider prov) {
        final boolean faking = stack.isFaking();
        if (faking)
            stack.setFaking(false);
        final CastingContext cc = prov.getCastingContext();
        final CompoundInvokable create = CompoundInvokable.create(description, -1, cc);
        if (faking)
            stack.setFaking(true);
        return create;
    }

    public static void nAtOut(final int n, final DataStack ds) {
        final Stack stack = ds.stack();
        final int stackIndex = Utils.stackIndex(stack, n);
        final Object res = stack.get(stackIndex);
        stack.remove(stackIndex);
        stack.push(res);
        // if (n > 0) {
        // final Stack st1 = new Stack();
        // for (int i = n; i-- > 0; st1.push(ds.pop()))
        // ;
        // final Object oo = ds.pop();
        // for (int i = n; i-- > 0; ds.push(st1.pop()))
        // ;
        // ds.push(oo);
        // }
    }

    public static void nAtIn(final int n, final DataStack ds) {
        final Stack stack = ds.stack();
        final int stackIndex = Utils.stackIndex(stack, n);
        final Object res = stack.pop();
        stack.insertElementAt(res, stackIndex);

        // if (n > 0) {
        // final Object oo = ds.pop();
        // final Stack st1 = new Stack();
        // for (int i = n; i-- > 0; st1.push(ds.pop()))
        // ;
        // ds.push(oo);
        // for (int i = n; i-- > 0; ds.push(st1.pop()))
        // ;
        // }
    }

    public static void yOut(final Object oo, final Object oo1, final DataStack ds) {
        ds.push(oo1);
        ds.push(oo);
    }

    public static Object get(final String s, final VariablesStorage vs) {
        return vs.get(s);
    }

    public static void param(final DataStack ds) {
        final int size = ds.top().st.size();
        if (size > 0)
            ds.param();
        else
            ds.param(1);
    }

    public static void param1(final DataStack ds) {
        Management.param(ds);
    }

    public static void param1(final DataStack ds, final int n) {
        ds.param(n);
    }

    public static void props(final DataStack ds, final String propcont) {
        try {
            final Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(propcont.getBytes("ISO-8859-1")));
            Management.frame(ds, properties);
        } catch (final Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static void excl(final Map m, final DataStack ds) {
        Management.frame(ds, m);
    }

    public static void excl(final CommandSource cs, final DataStack ds) {
        ds.param(1);
        Management.startObject(ds, cs, ds.pop());
    }

    private static void frame(final DataStack ds, final Map properties) {
        ds.frame(properties);
    }

    public static String props(final DataStack ds, final Map props) {
        try {

            final Properties properties = new Properties();
            final Set entrySet = props.entrySet();
            for (final Iterator iterator = entrySet.iterator(); iterator.hasNext();) {
                final Entry object = (Entry) iterator.next();
                final Object value = object.getValue();
                properties.setProperty(object.getKey().toString(), value != null ? value.toString() : null);
            }
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            properties.store(byteArrayOutputStream, null);
            return byteArrayOutputStream.toString("ISO-8859-1");
        } catch (final Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Map sub(final DataStack ds, final int n) {
        return ds.sub(n);
    }

    public static List array(final DataStack ds) {
        final Stack stack = ds.top().st;
        final ArrayList arrayList = new ArrayList(stack);
        stack.setSize(0);
        ds.deframe();
        return arrayList;
    }

    public static void import_package(final ClassPool classPool, final String string) {
        classPool.importPackage(string);
    }

    public static Object atX(final DataStack ds, final int n) {
        return ds.at(n);
    }

    public static int top(final DataStack ds) {
        return ds.top().st.size();
    }

    public static Object atXX(final DataStack ds, final int m, final int n) {
        return ds.at(m, n);
    }

    public static Link alink(final DataStack ds, int n) {
        FinalLink res = null;
        while (n-- > 0)
            res = new FinalLink(ds.pop(), res);
        return res;
    }

    public static void delink(final DataStack ds, final Link l) {
        for (Link res = l; res != null; res = res.next())
            ds.push(res.content());
    }

    public static void debug(final DataStack ds, final boolean b) {
        Englet.debug = b;
    }

    public static void debug(final DataStack ds, final int b) {
        Englet.debug = b != 0;
    }

    public static void debug2(final DataStack ds, final boolean b) {
        DataStack.DEBUG = b;
    }

    public static void debug2(final DataStack ds, final int b) {
        DataStack.DEBUG = b != 0;
    }

    public static void trace(final DataStack ds, final boolean b) {
        Trace.TRACE = b;
        if (!b)
            Trace.close();
    }

    public static void trace(final DataStack ds, final int b) {
        Management.trace(ds, b != 0);
    }

    public static void forgive(final MethodsStorage m, final boolean b) {
        MethodsStorage.forgiveErrors = b;
    }

    public static void stringize(final MethodsStorage m, final boolean b) {
        MethodsStorage.notFoundCommandAsString = b;
    }

    public static void fake(final DataStack stack, final boolean b) {
        stack.setFaking(b);
    }

    public static void fake(final DataStack stack, final int b) {
        stack.setFaking(b != 0);
    }

    public static void forgive(final MethodsStorage m, final int b) {
        MethodsStorage.forgiveErrors = b != 0;
    }

    public static void stringize(final MethodsStorage m, final int b) {
        MethodsStorage.notFoundCommandAsString = b != 0;
    }

    public static Englet englet(final DataStack ds, final MethodsStorage m, final ClassPool classPool) {
        return new Englet(ds.derive(), m, classPool);
    }

    public static Object run(final DataStack ds, final Englet englet, final Object object, final Link l)
            throws Throwable {
        return Utils.run(englet, object, l);
    }

    public static Invokable runner(final DataStack ds) throws Exception {
        return new MethodInvokable(Management.class.getMethod("run", new Class[] { DataStack.class, Englet.class,
                Object.class, Link.class }));
    }

    public static Link filter(final DataStack ds, final Link base, final Checker checker) {
        return Utils.filterLink(base, checker);
    }

    public static Class asClass(final ClassPool classPool, final String string) {
        return classPool.forName(string);
    }

    public static Invokable pure(final Invokable invokable, final DataStack stack) {
        final Stack stack2 = stack.top().st;
        final int newSize = stack2.size()
                - (invokable.parameterTypes().length + (invokable.targetType() != null ? 1 : 0));
        stack2.setSize(newSize > 0 ? newSize : 0);
        return invokable;

    }

    public static Object impl(final VariablesStorage vs, final String ifcn, final ClassPool pool) {
        return am.englet.Utils.constantProxy(vs, pool.forName(ifcn));
    }

    public static Object impl(final VariablesStorage vs, final Class ifc) {
        return am.englet.Utils.constantProxy(vs, ifc);
    }

    public static Invokable processor(final MethodsStorage methodsStorage, final ArgumentProvider provider,
            final String command, final ClassPool pool) {
        return Management.processor0(methodsStorage, provider, command, pool, false);

    }

    private static Invokable processor0(final MethodsStorage methodsStorage, final ArgumentProvider provider,
            final String command, final ClassPool pool, final boolean doNotLookUp) {
        final MethodRecord processor = methodsStorage.processor(command, provider);
        return processor != null ? processor.getMethod()
                : doNotLookUp || !Lookup.lookUp(methodsStorage, command, provider, pool) ? null : Management.processor0(
                        methodsStorage, provider, command, pool, true);
    }

    public static void add_url(final URL url, final ClassPool classPool) {
        classPool.addURL(url);
    }

    public static void setTopQiuet(final DataStack ds) {
        final boolean quiet = ds.top().quiet;
        if (quiet)
            ds.push(ds.at(1, -1));
        else
            ds.top().quiet = true;
    }

    public static void res(final DataStack ds, final int i) {
        ds.res(i);
    }

    public static Object lastX(final DataStack ds) {
        return ds.last(0);
    }

    public static Object lastY(final DataStack ds) {
        return ds.last(1);
    }

    public static Object lastZ(final DataStack ds) {
        return ds.last(2);
    }

    public static Object lastT(final DataStack ds) {
        return ds.last(3);
    }

    public static Object lastU(final DataStack ds) {
        return ds.last(3);
    }

    public static Object lastV(final DataStack ds) {
        return ds.last(3);
    }

    public static Object lastW(final DataStack ds) {
        return ds.last(3);
    }

    public static void ifgo(final CommandSource cs, final boolean b, final Link link) {
        Management.ifgo(cs, b, link, 0);
    }

    public static void ifgo(final CommandSource cs, final boolean b, final Link link, final int n) {
        if (b)
            cs.go(link, n);
    }

    public static Link goFakeLing(final CommandSource cs, final Link l) {
        return new Link() {

            public Object content() {
                cs.go(l, 0);
                return Const.NO_RESULT;
            }

            public Link next() {
                return null;
            }
        };
    }

    public static ServiceObject service_object(final SingletonPool sp, final ClassPool cp, final String cln) {
        return Management.service_object(sp, cp.forName(cln));
    }

    public static ServiceObject service_object(final SingletonPool sp, final Class forName) {
        return (ServiceObject) sp.getSingleton(forName);
    }

    public static void start1(final CommandSource cs, final DataStack ds, final Link link) {
        final Object peek = ds.peek();
        ds.frame();
        cs.start(Management.DEFRAMER_FINAL_LINK);
        ds.push(peek);
        cs.start(link);
    }

    public static void startns(final CommandSource cs, final DataStack ds, final Link link, final int n) {
        ds.frame().shading = true;
        ds.param(n);
        cs.start(Management.DEFRAMER_FINAL_LINK);
        cs.start(link);
    }

    public static void if_instead(final Object elsePart, final boolean b, final Link thenPart, final CommandSource cs,
            final DataStack ds) {
        if (!b)
            ds.push(elsePart);
        else
            cs.start(thenPart);
    }

    public static Chain chain(int n, final DataStack ds) {
        Chain res = null;
        while (n-- > 0)
            res = new Chain(ds.pop(), res);
        return res;
    }
}
