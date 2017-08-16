package am.englet.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import am.englet.$;

public class Utils {
    /**
     * @param cls
     * @param name
     * @return
     */
    public static Method lookUpGetterMethod(final Class cls, final String name) {
        return new $(cls) {
            public boolean check(final Method fld) {
                return true;
            }
        }.method(Modifier.STATIC, 0, new Class[0], new String[] { name, "is" + am.englet.Utils.toClassNameCase(name),
                "get" + am.englet.Utils.toClassNameCase(name) });
    }

    public static Method lookUpSetterMethod(final Class cls, final String name, final Class argType) {
        final String setterName = "set" + am.englet.Utils.toClassNameCase(name);
        final Method lookUpSetterMethod0 = lookUpSetterMethod0(cls, argType, setterName);
        return (lookUpSetterMethod0 != null) || argType.equals(String.class)
                || !CharSequence.class.isAssignableFrom(argType) ? lookUpSetterMethod0 : lookUpSetterMethod0(cls,
                String.class, setterName);
    }

    private static Method lookUpSetterMethod0(final Class cls, final Class argType, final String anObject) {
        return new $(cls) {
            public boolean check(final Method md) {
                Class[] parameterTypes;
                return !Modifier.isStatic(md.getModifiers()) && ((parameterTypes = md.getParameterTypes()).length == 1)
                        && parameterTypes[0].isAssignableFrom(argType) && md.getName().equals(anObject);
            }
        }.method();
    }

    /**
     * @param cls
     * @param name
     * @param isStatic
     *            TODO
     * @param includeDeclared
     * @param declaredOnly
     * @return
     */
    public static Field lookUpField(final Class cls, final String name, final boolean isStatic,
            final boolean includeDeclared, final boolean declaredOnly) {
        final String[] names = new String[] { name };
        final $ $1 = new $(cls) {
            public boolean check(final Field fld) {
                return true;
            }
        };
        final int sample = isStatic ? -1 : 0;
        Field field = declaredOnly ? null : $1.field(Modifier.STATIC, sample, names);
        if ((field == null) && includeDeclared)
            field = $1.declared.field(Modifier.STATIC, sample, names);
        if (field != null)
            field.setAccessible(true);
        return field;
    }
}
