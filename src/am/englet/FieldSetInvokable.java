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
public class FieldSetInvokable extends MemberInvokable {

    /**
     *
     */
    private static final long serialVersionUID = -7499262127809105627L;

    // final Class[] parameterTypes;

    public FieldSetInvokable(final Field field) {
        super(field);
        // parameterTypes = new Class[] { field.getType() };
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
        ((Field) member).set(obj, args[0]);
        return null;
    }

    public Class returnType(final Member member) {
        return void.class;
    }

    public Class[] parameterTypes(final Member member) {
        return new Class[] { ((Field) member).getType() };
    }

    public Class targetType(final Member member) {
        return Modifier.isStatic(member.getModifiers()) ? null : member
                .getDeclaringClass();
    }

    protected String type() {
        return "s";
    }

    protected String name(final Member member) {
        return ((Field) member).getName();
    }

}
