package am.englet;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public interface SingletonPool extends ServiceObject {

    public static final String SINGLETON_IMPLEMENTATIONS_PROPERTIES = "singletonImplementations.properties";
    public static final String DERIVED_FROM = ".derived.from";

    public static class util {

        public final static Map implMap = Collections.unmodifiableMap(new HashMap() {
            private static final long serialVersionUID = 1L;
            {
                final Properties p = sImpls();
                final Enumeration ns = p.propertyNames();
                while (ns.hasMoreElements()) {
                    final String n = ns.nextElement().toString();
                    if (!n.endsWith(SingletonPool.DERIVED_FROM))
                        try {
                            final Class asiigneeClass = getAssigneeClass(n);
                            final String v = p.getProperty(n);
                            final Class forName = util.getClass(asiigneeClass, v);
                            Utils.debug(System.out, "SingletonPool to put:", forName, " as:", asiigneeClass);
                            if (asiigneeClass.isAssignableFrom(forName))
                                put(asiigneeClass, forName);
                        } catch (final ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                }
            }
        });
        public final static Map derivationMap = Collections.unmodifiableMap(new HashMap() {
            private static final long serialVersionUID = 7523247821253002088L;

            {
                final Properties p = sImpls();
                final Enumeration ns = p.propertyNames();
                while (ns.hasMoreElements()) {
                    final String n = ns.nextElement().toString();
                    if (n.endsWith(SingletonPool.DERIVED_FROM))
                        try {
                            final String n2 = n.substring(0, n.length() - SingletonPool.DERIVED_FROM.length());
                            final Class asiigneeClass = getAssigneeClass(n2);
                            final String v = p.getProperty(n);
                            final String[] v1 = v.split(" ");
                            final boolean b = v1.length < 3;
                            if (v1.length < 4) {
                                final String v2 = v1[0];
                                final String v3 = b ? v2 : v1[1];
                                final Class forName = util.getClass(asiigneeClass, v2);
                                final Class forName1 = b ? forName : util.getClass(asiigneeClass, v3);
                                if (asiigneeClass.isAssignableFrom(forName)) {
                                    final Method m = v1.length < 2 ? null : forName1.getMethod(v1[v1.length - 1], null);
                                    final SingletonPool.Derivation value = new SingletonPool.Derivation(forName, m);
                                    Utils.debug(System.out, "SingletonPool to put:", value, " for:", asiigneeClass);
                                    put(asiigneeClass, value);
                                }
                            }
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                }
            }
        });

        private static Properties sImpls() {
            return Utils.getPropertiesByResource(SingletonPool.class,
                    SingletonPool.SINGLETON_IMPLEMENTATIONS_PROPERTIES);
        }

        private static Class getClass(final Class cl, final String v2) throws ClassNotFoundException {
            return Class.forName((v2.indexOf('.') > 0 ? v2 : cl.getPackage().getName() + '.' + v2));
        }

        private static Class getAssigneeClass(final String n) throws ClassNotFoundException {
            return util.getClass(SingletonPool.class, n);
        }

    }

    public abstract void putSingleton(final Object o);

    public abstract void putSingleton(final Object o, final Class cl);

    public abstract Object getSingleton(final Class singletonClass);

    public abstract Object findImplementation(final Class singletonClass);

    public final static Map implMap = util.implMap;

    static final class Derivation {
        public final Class clasz;

        public Derivation(final Class clasz, final Method method) {
            this.clasz = clasz;
            this.method = method;
        }

        public final Method method;

        public String toString() {
            return "Derivation [clasz=" + clasz + ", method=" + method + "]";
        }
    }

    public final static Map derivationMap = util.derivationMap;

}