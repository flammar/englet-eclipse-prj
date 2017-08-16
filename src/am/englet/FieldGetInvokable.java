/**
 * 20.11.2009
 *
 * 1
 *
 */
package am.englet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import am.englet.reflect.MemberInvokable;

/**
 * @author 1
 * 
 */
public class FieldGetInvokable extends MemberInvokable {

    /**
     *
     */
    private static final long serialVersionUID = 7898781532064172403L;

    public FieldGetInvokable(final Field field) {
        super(field);
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.Invokable#invoke(java.lang.Object, java.lang.Object[])
     */
    public Object invoke(final Member member, final Object obj,
            final Object[] args) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            InstantiationException {
        return ((Field) member).get(obj);
    }

    public Class returnType(final Member member) {
        return ((Field) member).getType();
    }

    public Class[] parameterTypes(final Member member) {
        return NO_CLASSES;
    }

    public Class targetType(final Member member) {
        return Modifier.isStatic(member.getModifiers()) ? null : member
                .getDeclaringClass();
    }

    protected String type() {
        return "g";
    }

    protected String name(final Member member) {
        return ((Field) member).getName();
    }

}
