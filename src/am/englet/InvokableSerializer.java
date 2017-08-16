package am.englet;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import am.englet.reflect.MemberInvokable;

public class InvokableSerializer {
    private static interface MemberProvider {
        Member member(final Class declaringType, final String name,
                final Class[] parameterTypes) throws SecurityException,
                NoSuchMethodException, NoSuchFieldException;
    }

    private final static Map creators = Collections
            .unmodifiableMap(new HashMap() {
                /**
         * 
         */
                private static final long serialVersionUID = 1L;

                {
                    try {
                        put(
                                "c",
                                ConstructorInvokable.class
                                        .getConstructor(new Class[] { Constructor.class }));
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        put("m", MethodInvokable.class
                                .getConstructor(new Class[] { Method.class }));
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        put("g", FieldGetInvokable.class
                                .getConstructor(new Class[] { Field.class }));
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        put("s", FieldSetInvokable.class
                                .getConstructor(new Class[] { Field.class }));
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            });

    private final static Map provs = Collections.unmodifiableMap(new HashMap() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        {
            put("c", new MemberProvider() {

                public Member member(final Class declaringType,
                        final String name, final Class[] parameterTypes)
                        throws SecurityException, NoSuchMethodException {
                    return declaringType.getConstructor(parameterTypes);
                }
            });
            put("m", new MemberProvider() {

                public Member member(final Class declaringType,
                        final String name, final Class[] parameterTypes)
                        throws SecurityException, NoSuchMethodException {
                    return declaringType.getMethod(name, parameterTypes);
                }
            });
            final MemberProvider fp = new MemberProvider() {

                public Member member(final Class declaringType,
                        final String name, final Class[] parameterTypes)
                        throws SecurityException, NoSuchMethodException,
                        NoSuchFieldException {
                    return declaringType.getField(name);
                }
            };
            put("g", fp);
            put("s", fp);
        }
    });

    static Invokable create(final Member m, final String type) {
        final Constructor object = (Constructor) creators.get(type);
        try {
            return (Invokable) object.newInstance(new Object[] { m });
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class SerializeInvokableDescription implements Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = 135170383000572607L;
        private String type;
        private String name;
        private Class declaringType;
        private Class[] parameterTypes;

        protected Object readResolve() throws ObjectStreamException {
            try {
                return create(((MemberProvider) provs.get(type)).member(
                        declaringType, name, parameterTypes), type);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static SerializeInvokableDescription describe(final String type,
            final String name, final MemberInvokable invokable) {
        final SerializeInvokableDescription description = new SerializeInvokableDescription();
        description.type = type;
        description.name = name;
        description.declaringType = invokable.declaringType();
        description.parameterTypes = invokable.parameterTypes();
        return description;
    }

}
