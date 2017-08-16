/**
 * 28.10.2009
 *
 * 1
 *
 */
package am.englet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import am.englet.cast.CastException;

//TODO: to be replaced with bean-pool-based and instantiation-engine-based implementation
/**
 * @author 1
 * 
 */
public class SimpleCastingContext implements CastingContext {

    public Object cast(final Class cls, final Object obj) {
        return (obj == null) || (cls == null /* BeanShell */)
                || cls.isInstance(obj) ? obj : doCast(cls, obj);
    }

    public boolean canCast(final Class target, final Class source) {
        Utils.debug(System.out, "canCast: ", source, " >> ", target);
        if (source == null /* && target != null */)
            return true;
        final boolean b = target.isAssignableFrom(source)
                || target.isAssignableFrom(Utils.deprimitivized(source))
                || (target.equals(CharSequence.class) && !byte.class
                        .equals(source.getComponentType()))
                || (target.equals(String.class) && CharSequence.class
                        .isAssignableFrom(source))
                || (target.equals(boolean.class) && (
                // Number.class.isAssignableFrom(source)
                // || source.equals(String.class) ||
                source.equals(Boolean.class)))
                || (target.equals(Boolean.class) && (
                // Number.class.isAssignableFrom(source)
                // || source.equals(String.class) ||
                source.equals(boolean.class)))
                || (target.equals(BigDecimal.class) && (Number.class
                // commented due to div(String, String)->Link
                        .isAssignableFrom(source) /*
                                                   * ||
                                                   * source.equals(String.class)
                                                   */))

                // || ((target.equals(int.class) ||
                // target.equals(Integer.class)) && Number.class
                // .isAssignableFrom(source))

                // || (!(source.equals(String.class) && (Number.class
                // .isAssignableFrom(target) || FinalLink.class
                // .isAssignableFrom(target))) && (!(target
                // .equals(String.class) && byte.class.equals(source
                // .getComponentType())) && (((new $(target) {
                // public boolean check(final Constructor con) {
                // return true;
                // }
                // }.constructor(new Class[] { source }) != null)) || (new $(
                // target) {
                // public boolean check(final Method con) {
                // return true;
                // }
                // }.method(Modifier.STATIC, -1, new Class[] { source }) !=
                // null))))
                || Utils.isUpCastable(target, source);
        Utils.debug(System.out, "canCast: ", source, " >> ", new Object[] {
                target, ": ", Boolean.valueOf(b) });
        return b;
    }

    // TODO: up-casting primitives and wrappers
    private Object doCast(final Class cls, final Object obj) {
        /*
         * if (cls.equals(String.class)) return obj != null ? obj.toString() :
         * "" + obj; else
         */
        if (cls.isInstance(obj))
            return obj;
        final Class deprimitivized = Utils.deprimitivized(cls);
        if (deprimitivized.isInstance(obj))
            return obj;
        else if (cls.equals(CharSequence.class))
            return toCharSequence(obj);
        else if (deprimitivized.equals(Boolean.class))
            return Boolean.valueOf(Utils.toBoolean(obj));
        else if (cls.equals(StringBuffer.class))
            return toStringBuffer(obj);
        else if (cls.equals(String.class) && (obj instanceof CharSequence))
            return obj.toString();
        else if (cls.equals(BigDecimal.class))
            return toBigDecimal(obj);
        else if ((deprimitivized.equals(Integer.class))
                && (obj instanceof Number))
            return toInt((Number) obj);

        final NumberCaster cstr = (NumberCaster) NUMBER_CASTERS
                .get(deprimitivized);
        if ((obj instanceof Number) && (cstr != null))
            return cstr.cast((Number) obj);
        else if (obj instanceof String)
            try {
                final String s = obj.toString();
                return Utils.tryInvocationalCasting(s, s.getClass(), cls);
            } catch (final Exception e) {
                // e.printStackTrace();
            }
        else
            try {
                return Utils.tryInvocationalCasting(obj, obj.getClass(), cls);
            } catch (final Exception e) {
                // e.printStackTrace();
            }
        throw new CastException("Cannot cast " + obj + " to class: " + cls);

    }

    private static Integer toInt(final Number obj) {
        return new Integer(obj.intValue());
    }

    static CharSequence toCharSequence(final Object o) {
        return o instanceof CharSequence ? (CharSequence) o : o.toString();
    }

    private static BigDecimal toBigDecimal(final Object o) {
        return o instanceof BigDecimal ? (BigDecimal) o
                : o instanceof Double ? new BigDecimal(((Double) o)
                        .doubleValue()) : new BigDecimal("" + o);
    }

    private static StringBuffer toStringBuffer(final Object o) {
        if (o instanceof StringBuffer)
            return (StringBuffer) o;
        else
            return new StringBuffer(String.valueOf(o));
    }

    static interface NumberCaster {
        Object cast(Number n);
    }

    private static final Map NUMBER_CASTERS;

    private static void put(final Map m, final Class cl, final NumberCaster c) {
        m.put(cl, c);
        final Class deprimitivized = Utils.deprimitivized(cl);
        if (deprimitivized != null)
            m.put(deprimitivized, c);
    }

    static {
        final HashMap m = new HashMap();
        put(m, int.class, new NumberCaster() {

            public Object cast(final Number n) {
                return new Integer(n.intValue());
            }
        });
        put(m, long.class, new NumberCaster() {

            public Object cast(final Number n) {
                return new Long(n.longValue());
            }
        });
        put(m, byte.class, new NumberCaster() {

            public Object cast(final Number n) {
                return new Byte(n.byteValue());
            }
        });
        put(m, short.class, new NumberCaster() {

            public Object cast(final Number n) {
                return new Short(n.shortValue());
            }
        });
        put(m, float.class, new NumberCaster() {

            public Object cast(final Number n) {
                return new Float(n.floatValue());
            }
        });
        put(m, double.class, new NumberCaster() {

            public Object cast(final Number n) {
                return new Double(n.doubleValue());
            }
        });
        put(m, BigInteger.class, new NumberCaster() {

            public Object cast(final Number n) {
                return BigInteger.valueOf(n.longValue());
            }
        });
        put(m, BigDecimal.class, new NumberCaster() {

            public Object cast(final Number n) {
                return new BigDecimal(n.doubleValue());
            }
        });
        NUMBER_CASTERS = Collections.unmodifiableMap(m);
    }

}
