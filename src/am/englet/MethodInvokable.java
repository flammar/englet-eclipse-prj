/**
 * 20.11.2009
 *
 * 1
 *
 */
package am.englet;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import am.englet.reflect.MemberInvokable;

/**
 * @author 1
 * 
 */
public class MethodInvokable extends MemberInvokable {

    /**
     *
     */
    private static final long serialVersionUID = 3882449745446249793L;
    private final Class p0class;

    public MethodInvokable(final Method method) {
        this(method, null);
    }

    public MethodInvokable(final Method method, final Class p0class) {
        super(method);
        // returnType = ((Method) member).getReturnType();
        // targetType = Modifier.isStatic(((Method) member).getModifiers()) ?
        // null
        // : ((Method) member).getDeclaringClass();
        this.p0class = p0class;
        // pts = deprimitivise(bpt(p0class));
    }

    protected Object invoke(final Member member2, final Object obj, final Object[] args)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        try {
            return ((Method) member2).invoke(obj, args);
        } catch (final IllegalArgumentException e) {
            dump(member2, args);
            throw e;
        } catch (final IllegalAccessException e) {
            dump(member2, args);
            throw e;
        } catch (final InvocationTargetException e) {
            dump(member2, args);
            throw e;
        }
    }

    private void dump(final Member member2, final Object[] args) {
        final List l = new ArrayList();
        for (int i = 0; i < args.length; i++)
            l.add((args[i] == null) ? (Object) "--null--" : (args[i].getClass()));
        Utils.outPrintln(System.out, "MethodInvokable.invoke() fail:member=" + member2 + ", args="
                + Arrays.asList(args) + ", classes=" + l);
    }

    private Class[] bpt(final Method member, final Class componentType) {
        final Class[] parameterTypes = (member).getParameterTypes();
        // array class hack
        final String name = member.getName();
        if (member.getDeclaringClass().equals(Array.class) && (parameterTypes.length > 0)
                && parameterTypes[0].equals(Object.class) && (name.startsWith("get") || name.startsWith("set")))
            // Object i Klasz frum
            parameterTypes[0] = componentType != null ? arrayClass(componentType) : arrayClass(
                    (member).getReturnType(), parameterTypes);
        return parameterTypes;
    }

    private Class arrayClass(final Class returnType, final Class[] parameterTypes) {
        final Class c1 = returnType.equals(void.class) ? parameterTypes[2] : returnType;
        return arrayClass(c1);
    }

    private Class arrayClass(final Class c1) throws NegativeArraySizeException {
        return Array.newInstance(c1, 0).getClass();
    }

    protected String type() {
        return "m";
    }

    protected String name(final Member member2) {
        return ((Method) member2).getName();
    }

    protected Class[] parameterTypes(final Member member2) {
        return MemberInvokable.deprimitivise(bpt((Method) member2, p0class));
    }

    protected Class returnType(final Member member2) {
        return ((Method) member2).getReturnType();
    }

    protected Class targetType(final Member member2) {
        return Modifier.isStatic(((Method) member2).getModifiers()) ? null : ((Method) member2).getDeclaringClass();
    }

    protected String extraToString() {
        return "(" + Arrays.asList(parameterTypes()) + ")";
    }

}
