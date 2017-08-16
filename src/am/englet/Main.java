/**
 * 
 */
package am.englet;

import java.lang.reflect.Constructor;
import java.util.Iterator;

/**
 * @author Adm1
 * 
 */
public class Main {

    private static Englet englet;

    /**
     * @param args
     * @throws Throwable
     */
    public static void main(final String[] args) throws Throwable {
        englet = new Englet();
        getSettings(args).apply(englet);
        // if (Englet.debug)
        // System.out.println(englet.getMethods());
        // englet.run();
        for (final Iterator i = englet.getStack().deframeAll().top().st
                .iterator(); i.hasNext();)
            System.out.println(i.next());

    }

    private static EngletSettings getSettings(final String[] args) {
        final String property = System.getProperty("englet.settings.class",
                SimpleStringArrayEngletSettings.class.getName());
        try {
            final Class forName = Class.forName(property);
            try {
                final Constructor constructor = forName
                        .getConstructor(new Class[] { String[].class });
                try {
                    return (EngletSettings) constructor
                            .newInstance(new Object[] { args });
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } catch (final SecurityException e) {
                e.printStackTrace();
            } catch (final NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                return (EngletSettings) forName.newInstance();
            } catch (final InstantiationException e) {
                e.printStackTrace();
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new SimpleStringArrayEngletSettings(args);
    }
}
