/**
 *
 */
package am.englet;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import am.englet.ArgumentProvider.ArgumentsAndTarget;
import am.englet.cast.ClassPool;
import am.englet.dispatch.DispatcherRecord;
import am.englet.link.FinalLink;
import am.englet.link.Link;
import am.englet.lookup.Lookup;

/**
 * @author Adm1
 * 
 */
public class MethodsStorage implements ServiceObject {
    public static class Getter implements Serializable {
        private static final long serialVersionUID = 8993449719081259509L;
        private final String varname;

        public String varname() {
            return varname;
        }

        public Getter(final String varname) {
            super();
            this.varname = varname;
        }

        public String toString() {
            return "get:" + varname;
        }

    }

    public static class Dispatcher {
        public String toString() {
            return Arrays.asList(records).toString();
        }

        MethodRecord[] records = new MethodRecord[0];

        public synchronized void put(final MethodRecord record) {
            // only non-service-object-type parameters are taken into account
            final int n = record.getParametersCount();
            final MethodRecord[] newrecs = new MethodRecord[records.length + 1];
            int i = 0;
            if (Englet.debug && (records.length > 0))
                Utils.outPrintln(System.out, "Dispatcher.put1:" + record);
            if (Englet.debug && (records.length == 1))
                Utils.outPrintln(System.out, "Dispatcher.put:" + records[0]);
            for (i = 0; i < records.length; i++)
                if (records[i].getParametersCount() <= n)
                    break;
            // System.out.println("put: i=" + i);
            if (i > 0)
                System.arraycopy(records, 0, newrecs, 0, i);
            if (i < records.length)
                System.arraycopy(records, i, newrecs, i + 1, records.length - i);
            newrecs[i] = record;
            Arrays.sort(newrecs, new Comparator() {

                public int compare(final Object o1, final Object o2) {
                    final MethodRecord m1 = (MethodRecord) o1;
                    final MethodRecord m2 = (MethodRecord) o2;
                    return Utils.paramTypesCompare(m1.getParameterTypes(), m2.getParameterTypes());
                }
            });
            records = newrecs;
            // System.out.println("put: this=" + toString());
        }

        public MethodRecord get(final Class[] sample, final CastingContext cont, final boolean simpleSingle) {
            return am.englet.dispatch.Utils.search(records, sample, cont, simpleSingle);
        }

        public int getMaxArgsCount() {
            return records.length < 1 ? 0 : records[0].getParametersCount();
        }
    }

    public final static class MethodRecord implements Invokable, DispatcherRecord {

        /**
         * @author 1
         * 
         */
        public interface InvokableMetadata {

            public Invokable invokable();

            // public boolean isStatic();
            //
            // public Class targetType();
        }

        protected Invokable method;

        public static class Type {
            public static final int PROCESSING = 0;
            public static final int MANAGEMENT = Type.PROCESSING + 1;
            public static final int IMMEDIATE = Type.MANAGEMENT + 1;

            public static final String toString(final int i) {
                return i == Type.PROCESSING ? "PROCESSING" : i == Type.MANAGEMENT ? "MANAGEMENT"
                        : i == Type.IMMEDIATE ? "IMMEDIATE" : "";
            }
        }

        final public int type;
        final public boolean isVoid;
        final public int argsCount;
        final public boolean isStatic;
        final public Class targetType;
        final public Class resultType;
        final public Class[] argTypes;
        // number of non-ServiceObject arguments
        final public Class[] nonSingletonArgTypes;
        final public int nonSingletonArgCount;

        public MethodRecord(final Invokable invokable, final int type) {
            method = /* invokableMetadata. */invokable/* () */;
            this.type = type;
            isVoid = (method.returnType() != null) && method.returnType().equals(Void.TYPE);
            argsCount = method.parameterTypes().length;
            targetType = method.targetType();
            isStatic = targetType == null; // invokableMetadata.isStatic();
            resultType = method.returnType();
            argTypes = method.parameterTypes();
            final List l = new ArrayList();
            for (int i = 0; i < argTypes.length; i++) {
                final Class class1 = argTypes[i];
                if ((class1 == null) || !ServiceObject.class.isAssignableFrom(class1))
                    l.add(class1);
            }
            nonSingletonArgTypes = (Class[]) l.toArray(new Class[0]);
            nonSingletonArgCount = nonSingletonArgTypes.length;
        }

        /**
         * @param o
         * @return wrapper for invokable object o
         */
        protected Invokable createInvocableImplInstance(final Object o) {
            return null;
        }

        /**
         * @return
         * @see java.lang.reflect.Method#getParameterTypes()
         */
        public final Class[] argTypes() {
            return copy(argTypes);
        }

        /**
         * @param argTypes2
         * @return
         */
        private Class[] copy(final Class[] argTypes2) {
            if (argTypes2.length == 0)
                return argTypes2;
            final Class[] res = new Class[argTypes2.length];
            System.arraycopy(argTypes2, 0, res, 0, argTypes2.length);
            return res;
        }

        public final boolean isImmediate() {
            return type == Type.IMMEDIATE;
        }

        public final boolean isProcessing() {
            return type == Type.PROCESSING;
        }

        public String toString() {
            return Type.toString(type) + " " + method.toString();
        }

        public Object invoke(final Object obj, final Object[] args) throws IllegalAccessException,
                IllegalArgumentException, InvocationTargetException, InstantiationException {
            try {
                final Object invoke = method.invoke(obj, args);
                Trace.success(invoke, obj, args, method);
                return invoke;
            } catch (final IllegalArgumentException e) {
                reportFail(obj, args, method, e);
                throw e;
            } catch (final IllegalAccessException e) {
                reportFail(obj, args, method, e);
                throw e;
            } catch (final InvocationTargetException e) {
                reportFail(obj, args, method, e);
                throw e;
            } catch (final InstantiationException e) {
                reportFail(obj, args, method, e);
                throw e;
            }
        }

        private void reportFail(final Object target, final Object[] args, final Invokable invokable, final Throwable e) {
            Utils.outPrintln(System.out, "MethodsStorage.MethodRecord.invoke():method=" + method);
            e.printStackTrace();
            e.printStackTrace(System.out);
            Trace.fail(target, args, invokable);
        }

        public Class[] getParameterTypes() {
            return copy(nonSingletonArgTypes);
        }

        int getParametersCount() {
            return nonSingletonArgCount;
        }

        final public Class returnType() {
            return method.returnType();
        }

        final public Class[] parameterTypes() {
            return method.parameterTypes();
        }

        /**
         * @return the isStatic
         */
        public boolean isStatic() {
            return isStatic;
        }

        /**
         * @return the targetType
         */
        public Class targetType() {
            return targetType;
        }

        public Invokable getMethod() {
            return method;
        }

    }

    public static class Direct implements Serializable {

        private static final long serialVersionUID = 8722602597085577848L;
        private final/* final */Object content;

        public Direct(final Object content) {
            super();
            this.content = content;
        }

        public String toString() {
            return "Direct:" + getContent();
        }

        public Object getContent() {
            return content;
        }
    }

    public static class Cast extends Direct {

        private static final long serialVersionUID = -399542682148718428L;
        public final Class castClass;

        public Cast(final Object content, final Class cls, final boolean nonstrict) {
            super(content);
            if (content == null) {
                if (!Link.class.equals(cls))
                    // throw new ClassCastException(
                    // "Can't cast null to not-Link class")
                    ;
            } else if (content instanceof InvokableDescription)
                ;
            else if (!nonstrict) {
                final Class deprimitivized = Utils.deprimitivized(cls);
                if ((deprimitivized != null) && !deprimitivized.isInstance(content)
                        && !(Number.class.isAssignableFrom(deprimitivized) && (content instanceof Number))
                        && !(cls.equals(String.class) && (content instanceof CharSequence)))
                    throw new ClassCastException("Can't cast " + content + " to " + cls);
            }
            castClass = cls;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "Cast [content=" + getContent() + ", to class=" + castClass + "]";
        }

    }

    public static class Training implements Serializable {

        public static class Invocation implements Serializable, Cloneable {
            // TODO final Felx jen em serialisable Companion

            public int result, target, arguments[];
            public Invokable invokable;

            public Invocation(final Object result, final ArgumentsAndTarget aat, final Invokable invokable) {
                this.result = System.identityHashCode(result);
                if (aat != null) {
                    target = System.identityHashCode(aat.target());
                    final Object[] arguments2 = aat.arguments();
                    arguments = new int[arguments2.length];
                    for (int i = 0; i < arguments2.length; i++)
                        arguments[i] = System.identityHashCode(arguments2[i]);
                } else
                    arguments = new int[target = 0];
                this.invokable = invokable;
            }

            protected Object clone() throws CloneNotSupportedException {
                final Invocation clone = (Invocation) super.clone();
                clone.arguments = (int[]) clone.arguments.clone();
                return clone;
            }
        }

        ArrayList data = new ArrayList();

        /**
         *
         */
        private static final long serialVersionUID = 1569196844449166630L;

        public void step(final Object result, final ArgumentsAndTarget aat, final Invokable invokable) {
            data.add(new Invocation(result, aat, invokable));
        }

        public void retain() {
            data.add(null);
        }

        public void direct(final Object result) {
            data.add(new Direct(result));
        }

        public Invocation[] data() {
            final Invocation[] invocations = (Invocation[]) data.toArray(new Invocation[0]);
            for (int i = 0; i < invocations.length; i++)
                try {
                    invocations[i] = (Invocation) invocations[i].clone();
                } catch (final CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            return invocations;

        }

    }

    private Training training;
    private final HashMap methods = new HashMap();
    public static boolean forgiveErrors;
    public static boolean notFoundCommandAsString;

    public void train() {
        training = new Training();
    }

    public void retain() {
        if (training != null)
            training.retain();
    }

    public Training trained() {
        try {
            return training;
        } finally {
            training = null;
        }
    }

    public String toString() {
        return "MethodsStorage [methods=" + methods + "]";
    }

    public MethodRecord get(final String key, final Class[] paramTypes, final CastingContext cont) {
        Utils.debug(System.out, "MethodsStorage.get(): key=", key);
        final Dispatcher dispatcher = (Dispatcher) methods.get(key);
        Utils.debug(System.out, "MethodsStorage.get(): dispatcher=", dispatcher);
        final char charAt = key.charAt(0);
        return dispatcher != null ? dispatcher.get(paramTypes, cont, (charAt < 'a') || (charAt > 'z')) : null;
        // return (MethodRecord) object;
    }

    public void put(final String key, final MethodRecord methodRecord) {
        final Dispatcher dispatcher = (Dispatcher) am.englet.Utils.getEnsuredValueByKey(methods, key.intern(),
                Dispatcher.class);
        dispatcher.put(methodRecord);
    }

    /**
     * @param command
     * @param handler
     * @param provider
     * @param varStor
     * @param classPool
     *            TODO
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     */
    public void invoke(final Object command, final ResultHandler handler, final ArgumentProvider provider,
            final VariablesStorage varStor, final ClassPool classPool) throws IllegalAccessException,
            InvocationTargetException, IllegalArgumentException, InstantiationException {
        Utils.debug(System.out, "invoke:command=", command);
        if (handleString(command, handler, provider, classPool) || handleDirect(command, handler)
                || handleGetter(command, handler, varStor))
            ;
        else {
            if (training != null)
                training.direct(command);
            handler.handleResult(Trace.unfound(command));
        }
    }

    private boolean handleString(final Object command, final ResultHandler handler, final ArgumentProvider provider,
            final ClassPool classPool) throws IllegalAccessException, InvocationTargetException,
            IllegalArgumentException, InstantiationException {
        if (!(command instanceof String))
            return false;
        try {
            if (MethodsStorage.forgiveErrors)
                try {
                    invokeString((String) command, provider, handler, classPool, false);
                } catch (final Exception e) {
                    Utils.outPrintln(System.err, "MethodsStorage.handleString():forgiven:fail:command:" + command);
                    (e instanceof InvocationTargetException ? ((InvocationTargetException) e).getTargetException() : e)
                            .printStackTrace();
                }
            else
                invokeString((String) command, provider, handler, classPool, false);
            return true;
        } catch (final IllegalArgumentException e) {
            repInvokeStringFail(command);
            throw e;
        } catch (final IllegalAccessException e) {
            repInvokeStringFail(command);
            throw e;
        } catch (final InvocationTargetException e) {
            repInvokeStringFail(command);
            throw e;
        } catch (final InstantiationException e) {
            repInvokeStringFail(command);
            throw e;
        } catch (final RuntimeException e) {
            repInvokeStringFail(command);
            throw e;
        }
    }

    /**
     * @param command
     */
    private void repInvokeStringFail(final Object command) {
        Utils.outPrintln(System.err, "MethodsStorage.handleString():fail:command:" + command);
    }

    private boolean handleDirect(final Object command, final ResultHandler handler) {
        if (!(command instanceof Direct))
            return false;
        final Object content = ((Direct) command).getContent();
        if (training != null)
            training.direct(content);
        handler.handleResult(Trace.directObject(content));
        return true;
    }

    private boolean handleGetter(final Object command, final ResultHandler handler, final VariablesStorage varStor) {
        if (!(command instanceof Getter))
            return false;
        final String varname = ((Getter) command).varname();
        handler.handleResult(Trace.got(varStor.get(varname), varname));
        return true;
    }

    public MethodRecord getMethodRecord(final String command, final ArgumentProvider prov, final boolean immediate) {
        final Dispatcher dispatcher = (Dispatcher) methods.get(command);
        final MethodRecord get0 = (dispatcher != null ? this.get(command, prov.getNNextArgumentTypes(dispatcher
                .getMaxArgsCount()), prov.getCastingContext()) : null);
        return (get0 != null) && (!immediate || get0.isImmediate()) ? get0 : null;
    }

    private static boolean handleNumberCandidate(final String command, final ResultHandler englet2) {
        try {
            englet2.handleResult(Integer.decode(command));
            return true;
        } catch (final NumberFormatException e) {
        }
        try {
            englet2.handleResult(Long.decode(command));
            return true;
        } catch (final NumberFormatException e) {
        }
        try {
            englet2.handleResult(Double.valueOf(command));
            return true;
        } catch (final NumberFormatException e) {
        }
        return false;
    }

    public void invokeString(final String command, final ArgumentProvider prov, final ResultHandler handler,
            final ClassPool classPool, final boolean parse) throws IllegalAccessException, InvocationTargetException,
            IllegalArgumentException, InstantiationException {
        final MethodRecord method = parse ? getMethodRecord(command, prov, true) : processor(command, prov);
        if ((method != null)) {
            Trace.command(command);
            MethodsStorage.invoke(prov, handler, method, training);
        } else if (parse) {
            final char charAt = command.charAt(0);
            if (charAt == '\'')
                handler.handleResult(new Direct(command.substring(1)));
            else if (charAt == '"')
                handler.handleResult(new Direct(command.substring(1, command.length() - 1)));
            else if ((charAt >= 'A') && (charAt <= 'Z'))
                handler.handleResult(new Getter(command.toLowerCase()));
            else if (!((((charAt >= '0') && (charAt <= '9')) || (charAt == '.') || (charAt == '-') || (charAt == '+')) && MethodsStorage
                    .handleNumberCandidate(command, handler)))
                handler.handleResult(command.intern());
        } else if (lookup(command, prov, classPool))
            invokeString(command, prov, handler, classPool, false);
        else if (MethodsStorage.notFoundCommandAsString || (command.length() < 3))
            handler.handleResult(Trace.unfoundString(command));
        else
            throw new CommandNotFoundException(command);
    }

    public MethodRecord processor(final String command, final ArgumentProvider prov) {
        return getMethodRecord(command, prov, false);
    }

    private boolean lookup(final String command, final ArgumentProvider prov, final ClassPool classPool) {
        // TODO sam Nam kev jen al en Adapt di
        return (command.length() > 2) && Lookup.lookUp(this, command, prov, classPool);
    }

    public static void invoke(final ArgumentProvider argProvider, final ResultHandler resultHandler,
            final Invokable methodRecord) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        MethodsStorage.invoke(argProvider, resultHandler, methodRecord, null);
    }

    public static void invoke(final ArgumentProvider argProvider, final ResultHandler resultHandler,
            final Invokable methodRecord, final Training training) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, InstantiationException {
        Object result;
        final ArgumentsAndTarget aat = argProvider.getArgumentsAndTarget(methodRecord.parameterTypes(), methodRecord
                .targetType());
        try {
            result = methodRecord.invoke(aat.target(), aat.arguments());
            if (training != null)
                if (methodRecord instanceof MethodRecord)
                    training.step(result, aat, ((MethodRecord) methodRecord).method);
        } catch (final IllegalArgumentException e) {
            Utils.outPrintln(System.out, "MethodsStorage.invoke() fail:" + aat);
            throw e;
        } catch (final IllegalAccessException e) {
            Utils.outPrintln(System.out, "MethodsStorage.invoke() fail:" + aat);
            throw e;
        } catch (final InvocationTargetException e) {
            Utils.outPrintln(System.out, "MethodsStorage.invoke() fail:" + aat);
            throw e;
        } catch (final InstantiationException e) {
            Utils.outPrintln(System.out, "MethodsStorage.invoke() fail:" + aat);
            throw e;
        } catch (final RuntimeException e) {
            Utils.outPrintln(System.out, "MethodsStorage.invoke() fail:" + aat);
            throw e;
        }
        final Class returnType = methodRecord.returnType();
        final boolean isVoid = void.class.equals(returnType);
        // final boolean isStatic = methodRecord.targetType() == null;
        Utils.debug(System.out, "MethodRecord.invoke:result:", (isVoid ? "-isVoid-" : result));
        aat.clean();

        if ((result != Const.NO_RESULT) && ((returnType == null) || !isVoid/* isVoid */)) {
            final Object result2 = result != null || returnType == null || returnType.equals(Link.class)
                    || returnType.equals(FinalLink.class) ? result : new Cast(result, returnType, false);
            resultHandler.handleResult(result2);
        }
    }

    public static class CommandNotFoundException extends RuntimeException {

        public CommandNotFoundException() {
            super();
        }

        public CommandNotFoundException(final String message, final Throwable cause) {
            super(message, cause);
        }

        public CommandNotFoundException(final String message) {
            super(message);
        }

        public CommandNotFoundException(final Throwable cause) {
            super(cause);
        }

        /**
         *
         */
        private static final long serialVersionUID = 1608669109189464775L;

    }
}
