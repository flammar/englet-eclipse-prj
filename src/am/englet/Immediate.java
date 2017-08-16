/**
 *
 */
package am.englet;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

import am.englet.MethodsStorage.Direct;
import am.englet.MethodsStorage.Getter;
import am.englet.cast.ClassPool;
import am.englet.link.FinalLink;
import am.englet.link.LinkUtils;

/**
 * @author Adm1
 * 
 */
public class Immediate {

    public static final String METHOD_NAME_REPLACEMENTS = "frame { chain }"
            + " integer int longv long floatv float doublev double perform !i bigdec #d run !run";

    public static void frame(final DataStack st, final CommandSource rs,
            final MethodsStorage ms) {
        st.frame();
    }

    public static FinalLink chain(final DataStack st) {
        st.enlist();
        final List l = (List) st.pop();
        st.deframe();
        return LinkUtils.ListAsFinalLinkChain(l);
    }

    public static void perform(final DataStack st, final MethodsStorage ms,
            final ArgumentProvider prov, final ResultHandler handler,
            final ClassPool classPool) throws Exception {
        final Object o = st.pop();
        try {
            perform(st, ms, o, prov, handler, classPool);
        } catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    private static void perform(final VariablesStorage st,
            final MethodsStorage ms, final Object o,
            final ArgumentProvider prov, final ResultHandler handler,
            final ClassPool classPool) throws IllegalAccessException,
            InvocationTargetException, IllegalArgumentException,
            InstantiationException {
        Utils.debug(System.out, "Immediate.perform:entered", "");
        if (o instanceof Direct)
            handler.handleResult(((Direct) o).getContent());
        else if (o instanceof Getter)
            handler.handleResult(st.get(((Getter) o).varname()));
        else {
            final String s = o.toString();
            ms.invokeString(s, prov, handler, classPool, false);
            // final MethodRecord method = ms.getMethodRecord(s, prov, false);
            // if (Englet.debug)
            // System.out.println("Immediate.perform:method:" + method);
            // if (method != null)
            // MethodsStorage.invoke(prov, handler, method);
            // else
            // handler.handleResult(s);
        }
    }

    public static Integer integer(final DataStack st, final CommandSource rs,
            final MethodsStorage ms) {
        final String s = popContent(st);
        final Integer i = new Integer(s);
        return i;
    }

    private static Object popContentObject(final DataStack st) {
        Object pop = st.pop();
        if (pop instanceof MethodsStorage.Direct)
            pop = ((Direct) pop).getContent();
        return pop;
    }

    private static String popContent(final DataStack st) {
        return popContentObject(st).toString();
    }

    public static Long longv(final DataStack st, final CommandSource rs,
            final MethodsStorage ms) {
        final String s = popContent(st);
        final Long i = new Long(s);
        return i;
    }

    public static Float floatv(final DataStack st, final CommandSource rs,
            final MethodsStorage ms) {
        final String s = popContent(st);
        final Float i = new Float(s);
        return i;
    }

    public static Double doublev(final DataStack st, final CommandSource rs,
            final MethodsStorage ms) {
        final String s = popContent(st);
        final Double i = new Double(s);
        return i;
    }

    public static BigDecimal bigdec(final DataStack st, final CommandSource rs,
            final MethodsStorage ms) {
        final String s = popContent(st);
        final BigDecimal i = new BigDecimal(s);
        return i;
    }

    public static void run(final CommandSource rstack,
            final ResultHandler resultHandler, final ArgumentProvider prov,
            final MethodsStorage methodsStorage,
            final VariablesStorage variablesStorage, final ClassPool classPool)
            throws Throwable {
        Englet.runCore(rstack, resultHandler, prov, methodsStorage,
                variablesStorage, classPool);
    }
}
