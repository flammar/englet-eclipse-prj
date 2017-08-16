/**
 * 20.11.2009
 *
 * 1
 *
 */
package am.englet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;

import am.englet.reflect.MemberInvokable;

/**
 * @author 1
 * 
 */
public class ConstructorInvokable extends MemberInvokable {

    /**
     *
     */
    private static final long serialVersionUID = -5852121443717514030L;

    public ConstructorInvokable(final Constructor con) {
        super(con);
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.Invokable#invoke(java.lang.Object, java.lang.Object[])
     */
    // public Object invoke(final Object obj, final Object[] args)
    // throws IllegalAccessException, IllegalArgumentException,
    // InvocationTargetException, InstantiationException {
    // final Member member2 = member;
    // return invoke(member2, null, args);
    // }

    protected Object invoke(final Member member2, final Object newParam, final Object[] args)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return ((Constructor) member2).newInstance(args);
    }

    public Class returnType(final Member member) {
        return declaringType()/* ((Constructor) member).getDeclaringClass() */;
    }

    protected Class[] parameterTypes(final Member member1) {
        return MemberInvokable.deprimitivise(((Constructor) member1).getParameterTypes());
    }

    public Class targetType(final Member member) {
        return null;
    }

    protected String type() {
        return "c";
    }

    protected String name(final Member member) {
        return "";
    }

}
