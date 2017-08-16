/**
 *
 */
package am.englet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import am.englet.MethodsStorage.Cast;
import am.englet.cast.ClassPool;

//TODO functionality as of layered singleton pool,
//like already made with string-to-object that of variables storage

/**
 * @author Adm1
 * 
 */
public class Englet implements ArgumentProvider, ResultHandler, SingletonPool {

    HashMap singletons = new HashMap();
    // private Object[] managementArgs;
    private static final Class[] managementArgClasses = new Class[] { DataStack.class, CommandSource.class,
            MethodsStorage.class };
    public static boolean debug = false;

    private static final Class[] CLASSES = new Class[0];

    // private CastingContext context;

    public Englet() {
        this(new DataStack(), new MethodsStorage(), new ClassPool());
        Management.adapt_management_class(Management.class.getName(), getMethods(),
                (ClassPool) getSingleton(ClassPool.class));
        Management.adapt_immediate_class(Immediate.class.getName(), getMethods(),
                (ClassPool) getSingleton(ClassPool.class));
    }

    // public Englet(final DataStack s, final MethodsStorage m) {
    // this(s, m, new ClassPool());
    // }

    public Englet(final DataStack s, final MethodsStorage m, final ClassPool classPool) {
        putSingleton(s);
        putSingleton(m);
        // putSingleton(this, ArgumentProvider.class);
        // putSingleton(this, ResultHandler.class);
        putSingleton(this, SingletonPool.class);
        putSingleton(classPool/* , ClassPool.class */);
        // putSingleton(getStack(), VariablesStorage.class);
        // // putSingleton(new StringIteratorFactory(), TokenizerFactory.class);
        // putSingleton(new EngletParserReaderTokenizerFactory(),
        // ServiceTokenizerFactory.class);
        // // TODO: 't's hot patch. To be replaced with broader version
        // setCastingContext(new SimpleCastingContext());
        // getRstack();
    }

    public void run() throws Throwable {
        final SingletonPool singPool = this;
        runCore(getRstack(), this, this, (MethodsStorage) singPool.getSingleton(MethodsStorage.class),
                (VariablesStorage) singPool.getSingleton(VariablesStorage.class), (ClassPool) singPool
                        .getSingleton(ClassPool.class));
    }

    public static void runCore(final CommandSource rstack, final ResultHandler resultHandler,
            final ArgumentProvider prov, final MethodsStorage methodsStorage, final VariablesStorage variablesStorage,
            final ClassPool classPool) throws Throwable {
        try {
            while (rstack.tryNext()) {
                final Object o = rstack.content();
                methodsStorage.invoke(o, resultHandler, prov, variablesStorage, classPool);
            }
        } catch (final IllegalArgumentException e) {
            dump(rstack, variablesStorage);
            throw e;
        } catch (final IllegalAccessException e) {
            dump(rstack, variablesStorage);
            throw e;
        } catch (final InvocationTargetException e) {
            dump(rstack, variablesStorage);
            throw e.getTargetException();
        } catch (final InstantiationException e) {
            dump(rstack, variablesStorage);
            throw e;
        } catch (final Throwable e) {
            dump(rstack, variablesStorage);
            throw e;
        }
    }

    /**
     * @param rstack
     * @param variablesStorage
     */
    private static void dump(final CommandSource rstack, final VariablesStorage variablesStorage) {
        Utils.outPrintln(System.err, "Englet.runCore() fail");
        Utils.outPrintln(System.err, "Englet.runCore():rstack:" + rstack);
        Utils.outPrintln(System.err, "Englet.runCore():variablesStorage:" + variablesStorage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * am.englet.linkbasedwired.ArgumentProvider#getArgumentsAndTarget(java.
     * lang.Class[], java.lang.Class)
     */
    public ArgumentsAndTarget getArgumentsAndTarget(final Class[] argTypes, final Class targetType) {
        /* args are to be popped before target */
        // BeanShell methods which have parameter types equal to null also shall
        // be correctly handled
        getStack().popReset();
        final int argsCount = argTypes != null ? argTypes.length : 0;
        final Object args[] = new Object[argsCount];
        for (int i1 = argsCount; i1-- > 0;)
            args[i1] = getArgument(argTypes[i1]);
        final Object target = targetType == null ? null : getArgument(targetType);
        final ArgumentsAndTarget aat = new ArgumentsAndTarget() {

            private final Object[] filledArgs0 = args;
            Object target0 = target;

            public Object target() {
                return target0;
            }

            public Object[] arguments() {
                return filledArgs0;
            }

            public void clean() {
                final Object o[] = filledArgs0;
                final int length = o.length;
                for (int i = 0; i < length; i++)
                    o[i] = null;
                target0 = null;
            }

            public String toString() {
                return super.toString() + "ArgumentsAndTarget[ target=" + target0 + ", args="
                        + Arrays.asList(filledArgs0) + ']';
            }
        };
        return aat;
    }

    public static void parse(final String commands, final ArgumentProvider prov, final ResultHandler handler,
            final TokenizerFactory parserFactory, final MethodsStorage methods) throws Exception {
        final Iterator parser = parserFactory.forObject(commands);
        while (parser.hasNext())
            methods.invokeString(("" + parser.next()), prov, handler, null, true);
    }

    public void handleResult(final Object o) {
        getStack().push(o);
    }

    /**
     * Method that uses at least one "basic" singleton and has no Englet type
     * argument.
     * 
     * @param m0
     * @return
     */
    public static boolean isManagementMethod(final Object m0) {
        // TODO: bring to better order determining whether method has an
        // argument of singleton type
        if (!(m0 instanceof Method))
            return false;
        final Method m = (Method) m0;
        final Class[] parameterTypes = m.getParameterTypes();
        final Set mclasses = new HashSet(Arrays.asList(new Class[] { DataStack.class, CommandSource.class,
                MethodsStorage.class }));
        // mclasses.addAll(sing);
        for (int i = 0; i < parameterTypes.length; i++) {
            final Class cls = parameterTypes[i];
            // if ()
            // return false;
            if (!cls.equals(Englet.class) && (mclasses.contains(cls) || ServiceObject.class.isAssignableFrom(cls)))
                return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.ArgumentProvider#getArgument()
     */
    public Object getArgument() {
        final DataStack stack = getStack();
        final Object pop = stack.pop();
        Utils.debug(System.out, "Englet.getArgument():pop:", pop);
        return pop instanceof Cast ? ((Cast) pop).getContent() : pop;
    }

    public static Class[] managementArgClasses() {
        return managementArgClasses;
    }

    public MethodsStorage getMethods() {
        return (MethodsStorage) getSingleton(MethodsStorage.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * am.englet.linkbasedwired.SingletonPool#putSingleton(java.lang.Object)
     */
    public void putSingleton(final Object o) {
        singletons.put(o.getClass(), o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * am.englet.linkbasedwired.SingletonPool#putSingleton(java.lang.Object,
     * java.lang.Class)
     */
    public void putSingleton(final Object o, final Class cl) {
        // cannot add any Englet keyed by Englet.class
        if (cl.equals(getClass()))
            throw new IllegalArgumentException("Cannot add anything keyed by Englet class");
        if (cl.isInstance(o))
            singletons.put(cl, o);
        else
            throw new IllegalArgumentException("Singleton: " + o + " is not instance of type:" + cl
                    + " to be keyed by.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.linkbasedwired.SingletonPool#getSingleton(java.lang.Class)
     */
    public Object getSingleton(final Class singletonClass) {
        final Object implementation = findImplementation(singletonClass);
        return implementation != null ? implementation : Utils.getEnsuredValueByClassKey(singletons, singletonClass,
                (Class) (util.implMap.containsKey(singletonClass) ? util.implMap.get(singletonClass) : singletonClass));
    }

    public DataStack getStack() {
        return (DataStack) getSingleton(DataStack.class);
    }

    public CommandSource getRstack() {
        return (CommandSource) getSingleton(CommandSource.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.ArgumentProvider#getArgument(java.lang.Class)
     */
    public Object getArgument(final Class clazz) {
        // BeanShell methods which have parameter types equal to null also shall
        // be correctly handled
        final Object object = clazz == null ? null : singletons.get(clazz);
        try {
            return cast(clazz,
                    (object == null)
                            && ((clazz == null) || Englet.class.equals(clazz) || !ServiceObject.class
                                    .isAssignableFrom(clazz)) ? getArgument()
                            : object == null ? getServiceObject(clazz) : object);
        } catch (final RuntimeException e) {
            Utils.outPrintln(System.err, "getArgument(" + clazz + "): fail");
            throw e;
        }
    }

    private Object getServiceObject(final Class clazz) {
        final Object impl = findImplementation(clazz);
        if (impl != null)
            return impl;
        else {
            final SingletonPool.Derivation d = (Derivation) util.derivationMap.get(clazz);
            if (d != null) {
                final Object serviceObject1 = getServiceObject(d.clasz);
                if (d.method != null)
                    try {
                        final Object invoke = d.method.invoke(serviceObject1, null);
                        putSingleton(invoke, clazz);
                        return invoke;
                    } catch (final Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                else {
                    putSingleton(serviceObject1, clazz);
                    return serviceObject1;
                }
            } else {
                final Class clazz1 = (Class) util.implMap.get(clazz);
                return (Utils.getEnsuredValueByClassKey(singletons, clazz, (clazz1 != null ? clazz1 : clazz)));
            }
        }
    }

    private Object cast(final Class clazz, final Object object2) {
        final CastingContext context = getCastingContext();
        return context != null ? context.cast(clazz, object2) : object2;
    }

    public CastingContext getCastingContext() {
        // return context;
        return (CastingContext) getSingleton(CastingContext.class);
    }

    public void setCastingContext(final CastingContext context) {
        putSingleton(context, CastingContext.class);
        // this.context = context;
    }

    public ServiceTokenizerFactory getParserFactory() {
        return (ServiceTokenizerFactory) getSingleton(ServiceTokenizerFactory.class);
        // return parserFactory;
    }

    public void parse(final String property) throws Exception {
        final ArgumentProvider ap = (ArgumentProvider) getSingleton(ArgumentProvider.class);

        final ResultHandler rh = (ResultHandler) getSingleton(ResultHandler.class);
        Management.parse(property, ap, rh,
                ((ServiceTokenizerFactory) this.getSingleton(ServiceTokenizerFactory.class)), ((MethodsStorage) this
                        .getSingleton(MethodsStorage.class)));
    }

    public Class[] getNNextArgumentTypes(final int n) {
        if (n == 0)
            return CLASSES;
        return getStack().nTopArgumentTypes(n);
        // final DataStack stack = getStack();
        // final Stack st = stack.top().st;
        // final int nn = st.size();
        // if (n > nn)
        // n = nn;
        // final Class[] res = new Class[n];
        // for (int i = n, j = nn; i-- > 0;) {
        // final Object object = st.get(--j);
        // res[i] = object != null ? object.getClass() : null;
        // }
        // return res;
    }

    public Object findImplementation(final Class singletonClass) {
        final Object result = singletons.get(singletonClass);
        if (result == null && !(Englet.class.equals(singletonClass)))
            for (final Iterator iterator = singletons.entrySet().iterator(); iterator.hasNext();) {
                final Object value = ((Entry) iterator.next()).getValue();
                if (singletonClass.isInstance(value)) {
                    putSingleton(value, singletonClass);
                    return getSingleton(singletonClass);
                }
            }
        return result;
    }
}
