package am.englet.reflect;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;

import am.englet.Invokable;
import am.englet.InvokableSerializer;
import am.englet.Utils;

public abstract class MemberInvokable implements Invokable, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1743781301239475144L;
    private final Member member;
    private Class[] pts;
    private Class returnType;
    private Class targetType;
    private String name;

    /**
     * @param con
     */
    public MemberInvokable(final Member con) {
        member = con;
        if (con instanceof AccessibleObject)
            ((AccessibleObject) con).setAccessible(true);
        // pts = memberParameterTypes(member);
    }

    protected abstract Class[] parameterTypes(Member member2);

    public Member getMember() {
        return member;
    }

    public final String toString() {
        return Utils.simpleClassname(getClass().getName()) + ": " + member + extraToString();
    }

    protected String extraToString() {
        return "";
    }

    protected Object writeReplace() {
        return InvokableSerializer.describe(type(), name(), this);
    }

    protected abstract String name(Member member2);

    protected abstract String type();

    public final Class declaringType() {
        return member.getDeclaringClass();
    }

    protected static final Class[] deprimitivise(final Class[] pts) {
        final int k = pts.length;
        if (k == 0)
            return pts;
        final Class[] res = new Class[k];
        System.arraycopy(pts, 0, res, 0, k);
        for (int i = 0; i < pts.length; i++)
            res[i] = Utils.deprimitivized(pts[i]);
        return res;
    }

    public final Class[] parameterTypes() {
        if (pts == null)
            pts = parameterTypes(member);
        final int k = pts.length;
        if (k == 0)
            return pts;
        final Class[] res = new Class[k];
        System.arraycopy(pts, 0, res, 0, k);
        return res;
    }

    public final Class returnType() {
        if (returnType == null)
            returnType = returnType(member);
        return returnType;
    }

    protected abstract Class returnType(Member member2);

    public final Class targetType() {
        if (targetType == null)
            targetType = targetType(member);
        return targetType;
    }

    protected abstract Class targetType(Member member2);

    protected final String name() {
        if (name == null)
            name = name(member);
        return name;
    }

    protected abstract Object invoke(final Member member2, final Object obj, final Object[] args)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException;

    public final Object invoke(final Object obj, final Object[] args) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, InstantiationException {
        return invoke(member, obj, args);
    }
}