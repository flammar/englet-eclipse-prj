package am.englet.lookup;

import java.util.Enumeration;
import java.util.Properties;

public class SimpleInvokableCandidateSourceFactory implements InvokableCandidateSourceFactory {

    public interface Setter {
        void set(Class class1, String memberName, Object value, Object object) throws Exception;
    }

    public InvokableCandidateSource getInstance(final Properties pr) {
        final String property = pr.getProperty("class");
        final int indexOf = property.indexOf('.');
        final boolean b = indexOf >= 0;
        final String cln = b ? property : this.getClass().getPackage().getName() + '.' + property;
        Class forName;
        try {
            forName = Class.forName(cln);
            final Enumeration propertyNames = pr.propertyNames();
            while (propertyNames.hasMoreElements()) {
                final String str = (String) propertyNames.nextElement();
                try {
                    final Object newInstance = forName.newInstance();
                    setValue(forName, str, pr.getProperty(str), newInstance);
                    return (InvokableCandidateSource) newInstance;
                } catch (final InstantiationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (final IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (final Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (final ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return null;
    }

    private static String toCamelCase(final String memberName) {
        return Lookup.underscoredToCamel(memberName.replace('.', '_'), false);
    }

    private void setValue(final Class forName, final String fn, final Object value, final Object newInstance)
            throws IllegalArgumentException, IllegalAccessException {
        final Setter[] ss = new Setter[] { new Setter() {

            public void set(final Class class1, final String memberName, final Object value, final Object object)
                    throws Exception {
                class1.getField(toCamelCase(memberName)).set(object, value);
            }

        }, setterWithPrefix("set_"), setterWithPrefix("") };
        for (int i = 0; i < ss.length; i++) {
            final Setter setter = ss[i];
            try {
                setter.set(forName, fn, value, newInstance);
                return;
            } catch (final Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    private Setter setterWithPrefix(final String prefix) {
        return new Setter() {

            public void set(final Class class1, final String memberName, final Object value, final Object object)
                    throws Exception {
                final String string = prefix + memberName;
                class1.getMethod(toCamelCase(string), new Class[] { String.class }).invoke(object,
                        new Object[] { value });
            }
        };
    }

}
