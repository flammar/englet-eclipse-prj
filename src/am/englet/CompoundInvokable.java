/**
 * 08.12.2009
 *
 * 1
 *
 */
package am.englet;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import am.englet.MethodsStorage.Direct;

// TODO compound if conditional

/**
 * @author 1
 * 
 */
public class CompoundInvokable implements Invokable, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3547209103371636553L;
    private InvokableWrapper main;
    private Class[] parameterTypes;

    public static CompoundInvokable create(final InvokableDescription desc, final int mode, final CastingContext cc) {
        desc.prepare();
        final CreationContext creationContext = new CreationContext();
        final InvokableWrapper create = CompoundInvokable.create(desc, mode, creationContext, cc);
        final CompoundInvokable res = new CompoundInvokable();
        res.main = create;
        res.parameterTypes = (Class[]) creationContext.argClasses.toArray(new Class[0]);
        return res;
    }

    private static InvokableWrapper create(final InvokableDescription desc, final int mode,
            final CreationContext creationContext, final CastingContext cc) {
        final Invokable staticInvokable = Utils.toStatic(desc.getInvokable(), mode);
        return new InvokableWrapper(staticInvokable, CompoundInvokable.prepareArgumentSources(mode, creationContext,
                desc.getSources(), staticInvokable.parameterTypes(), cc), desc.getMultiKey(), mode, cc);
    }

    private static ArgumentSource[] prepareArgumentSources(final int mode, final CreationContext creationContext,
            final Object[] sources, final Class[] parameterTypes2, final CastingContext cc) {
        final ArgumentSource[] ss = new ArgumentSource[sources.length];
        for (int i = 0; i < sources.length; i++)
            ss[i] = (sources[i] == null) ? creationContext.forClass(parameterTypes2[i])
                    : (sources[i] instanceof Direct) ? new DirectArgSource((Direct) sources[i]) : CompoundInvokable
                            .createInvokableWrappwerArgSource((InvokableDescription) sources[i], mode, creationContext,
                                    cc);
        return ss;
    }

    private static ArgumentSource createInvokableWrappwerArgSource(final InvokableDescription invokableDescription,
            final int mode, final CreationContext creationContext, final CastingContext cc) {
        final ArgumentSource visited = creationContext.visited(invokableDescription);
        final ArgumentSource argumentSource = visited != null ? visited : creationContext
                .visit(invokableDescription, new InvokableWrapperArgSource(CompoundInvokable.create(
                        invokableDescription, mode, creationContext, cc)));
        return argumentSource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        final String string = super.toString();
        final StringBuffer buffer = new StringBuffer();
        final int length = parameterTypes.length;
        for (int i = 0; i < length; i++)
            buffer.append(',').append(
                    parameterTypes[i] != null ? (Object) parameterTypes[i].getName() : parameterTypes[i]);
        (length > 0 ? buffer.insert(1, '(').replace(0, 1, string).append(')') : buffer.append(string).append("()"))
                .append(main);
        return buffer.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.Invokable#invoke(java.lang.Object, java.lang.Object[])
     */
    public Object invoke(final Object obj, final Object[] args) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, InstantiationException {
        final InvocationData data = new InvocationData(args);
        return main.invoke(data);
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.Invokable#parameterTypes()
     */
    public Class[] parameterTypes() {
        return (Class[]) Utils.copy(parameterTypes);
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.Invokable#returnType()
     */
    public Class returnType() {
        return main.invokable.returnType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.Invokable#targetType()
     */
    public Class targetType() {
        return null;
    }

    public static class InvokableWrapper implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 9028831022794900334L;
        private final ArgumentSource sources[];
        final Invokable invokable;
        private final Class[] argTypes;
        // jev null onT kfur bi invoke-Ung Kwem Aj ge 1 nax I o Ung'sz Ny ri em
        // Map inem Ung em Key ai
        private final Integer multiKey;
        final int argCount;
        private final CastingContext cctx;

        /**
         * @param invokable
         */
        public InvokableWrapper(final Invokable invokable, final ArgumentSource sources[], final Integer multiKey,
                final int mode, final CastingContext cc) {
            this.invokable = Utils.toStatic(invokable, mode);
            argTypes = (Class[]) Utils.copy(this.invokable.parameterTypes());
            this.sources = sources;
            this.multiKey = multiKey;
            argCount = invokable.parameterTypes().length;
            cctx = cc != null ? cc : new CastingContext() {

                public Object cast(final Class cls, final Object obj) {
                    return obj;
                }

                public boolean canCast(final Class target, final Class source) {
                    return target != null ? target.equals(source) : target == source;
                }
            };
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        public String toString() {
            final StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < sources.length; i++)
                buffer.append(',').append(sources[i]);
            buffer.replace(0, 1, "(").append(")[").append(multiKey).append(']').append(invokable);
            return buffer.toString();
        }

        public Object invoke(final InvocationData data) throws IllegalArgumentException, IllegalAccessException,
                InvocationTargetException, InstantiationException {
            if ((multiKey != null) && data.worked.containsKey(multiKey))
                return data.worked.get(multiKey);
            else {
                final Object args[] = new Object[argCount];
                for (int i = 0; i < sources.length; i++) {
                    final Object data2 = sources[i].getData(data);
                    args[i] = data2 != null ? (data2.getClass().equals(argTypes[i]) ? data2 : cctx.cast(argTypes[i],
                            data2)) : null;
                }
                am.englet.Utils.debug(System.out, "CompoundInvokable.InvokableWrapper.invoke():", invokable, " with ",
                        new CodeBlock() {

                            public Object result() {
                                return (args == null ? (Object) args : Arrays.asList(args));
                            }
                        });
                Object result;
                result = invokable.invoke(null, args);
                if ((multiKey != null))
                    data.worked.put(multiKey, result);
                return result;
            }
        }
    }

    public static interface ArgumentSource extends Serializable {
        public Object getData(InvocationData invocationData) throws IllegalArgumentException, IllegalAccessException,
                InvocationTargetException, InstantiationException;
    }

    static class InvocationData {
        final Object[] args;
        final Map worked = new HashMap();

        /**
         * @param args
         */
        public InvocationData(final Object[] args) {
            this.args = args != null ? Utils.copy(args) : new Object[0];
        }

    }

    public final static class ArgArrayArgSource implements ArgumentSource {
        /**
         *
         */
        private static final long serialVersionUID = -8266447667794650111L;
        private final int l;

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "argument[" + l + "]";
        }

        public ArgArrayArgSource(final int l) {
            this.l = l;
        }

        public Object getData(final InvocationData invocationData) {
            return invocationData.args[l];
        }
    }

    public final static class InvokableWrapperArgSource implements ArgumentSource {
        /**
         *
         */
        private static final long serialVersionUID = 3446685134354159387L;
        private final InvokableWrapper w;

        /**
         * @param w
         */
        public InvokableWrapperArgSource(final InvokableWrapper w) {
            this.w = w;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return w.toString();
        }

        public Object getData(final InvocationData invocationData) throws IllegalArgumentException,
                IllegalAccessException, InvocationTargetException, InstantiationException {
            return w.invoke(invocationData);
        }
    }

    public final static class DirectArgSource implements ArgumentSource {
        /**
         *
         */
        private static final long serialVersionUID = 6214110816249642492L;
        private final Direct d;

        /**
         * @param d
         */
        public DirectArgSource(final Direct d) {
            this.d = d;
        }

        public Object getData(final InvocationData invocationData) {
            return d.getContent();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return d.toString();
        }
    }

    static class CreationContext {
        List argClasses = new ArrayList();
        Map visited = new HashMap();

        ArgumentSource visited(final InvokableDescription description) {
            return (ArgumentSource) visited.get(description);
        }

        ArgumentSource visit(final InvokableDescription description, final ArgumentSource w) {
            visited.put(description, w);
            return w;
        }

        ArgumentSource forClass(final Class cls) {
            final int l = argClasses.size();
            argClasses.add(cls);
            return new ArgArrayArgSource(l);
        }
    }

}
