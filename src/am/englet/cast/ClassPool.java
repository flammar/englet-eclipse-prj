/**
 * 03.11.2009
 *
 * 1
 *
 */
package am.englet.cast;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import am.englet.ServiceObject;
import am.englet.Utils;

/**
 * @author 1
 * 
 */
public class ClassPool implements ServiceObject {

    private static final String BASE_PKGS = "java.applet, java.awt, java.awt.datatransfer, "
            + "java.awt.event, java.awt.image, java.beans, java.io, "
            + "java.lang, java.lang.reflect, java.math, java.net, "
            + "java.rmi, java.rmi.dgc, java.rmi.registry, java.rmi.server, "
            + "java.sql, java.text, java.util, java.util.zip, java.util.regex, "
            + "java.nio, java.nio.charset am.englet am.englet.link am.englet.util ";

    // Package-name-to-package-url-path map
    private final Map importedPackages = new HashMap();
    private final Map classes = new HashMap();
    private final Map impls = new HashMap();

    private final classLoader classLoader = /* this. */new classLoader(new URL[0], ClassLoader.getSystemClassLoader());

    {
        importPackages(BASE_PKGS.replaceAll(", ", " ").trim().split(" "));
        // importedPackages.put("", "");
        final Class[] prims = new Class[] { int.class, long.class, byte.class, short.class, float.class, double.class,
                char.class, boolean.class, void.class };
        for (int i = 0; i < prims.length; i++)
            classes.put(prims[i].getName(), prims[i]);
    }

    public void importPackages(final String[] pkgName) {
        for (int i = 0; i < pkgName.length; i++)
            importPackage(pkgName[i]);
    }

    public void importAlias(final String alias, final String classs) {
        final Class forName = forName(classs);
        if (forName != null)
            classes.put(alias, forName);
    }

    public Class forName(final String name) {
        if (name.endsWith("[]")) {
            final Class forName = forName(name.substring(0, name.length() - 2));
            if (forName != null) {
                final Class class1 = Array.newInstance(forName, 0).getClass();
                classes.put(name, class1);
                return class1;
            }
        }
        final Object object = classes.get(name);
        final Class class1 = object == null ? findClass(name) : (Class) object;
        return class1;
    }

    public Class getImpl(final Class superClass) {
        final Object subClass = impls.get(superClass);
        return (Class) (subClass != null ? subClass : superClass);
    }

    public void setImpl(final Class superClass, final Class subClass) {
        if ((superClass != null) && (subClass != null))
            impls.put(superClass, subClass);
    }

    public void addImpl(final Class subClass) {
        if (subClass == null)
            return;
        final Set assignTargetsSet = Utils.assignTargetsSet(subClass);
        for (final Iterator iterator = assignTargetsSet.iterator(); iterator.hasNext();)
            setImpl((Class) iterator.next(), subClass);
    }

    public void importPackage(final String pkgName) {
        importedPackages.put(pkgName + '.', Utils.packageNameToPackagePath(pkgName) + "/");
    }

    private Class findClass(final String name) {
        if (name.indexOf('.') >= 0) {
            final Class res = tryResource(Utils.simpleClassname(name), name, Utils.packageNameToPackagePath(name)
                    + ".class");
            if (res != null)
                return res;
        } else {
            final Iterator iterator2 = foundClassesIterator(name);
            return iterator2.hasNext() ? (Class) iterator2.next() : null;
        }
        return null;
    }

    public Iterator foundClassesIterator(final String name) {
        final Iterator iterator = importedPackages.keySet().iterator();
        return new Iterator() {
            Class nextValue = Object.class;
            boolean wasNext = true;

            public boolean hasNext() {
                provideNextValue1();
                wasNext = false;
                return nextValue != null;
            }

            public Object next() {
                provideNextValue1();
                wasNext = true;
                return nextValue;
            }

            private void provideNextValue1() {
                if (wasNext && (nextValue != null))
                    nextValue = nextValue();
            }

            private Class nextValue() {
                while (iterator.hasNext()) {
                    final String pkg = (String) iterator.next();
                    final Class res1 = tryResource(name, pkg + name, importedPackages.get(pkg) + name + ".class");
                    if (res1 != null)
                        return res1;
                }
                return null;
            }

            public void remove() {
            }
        };
    }

    /**
     * @param name
     * @param fullClassName
     * @param resourcePath
     * @return
     */
    private Class tryResource(final String name, final String fullClassName, final String resourcePath) {
        Class res = null;
        final URL resource = getResource(resourcePath);
        if (resource != null)
            try {
                final Class forName = Class.forName(fullClassName, true, classLoader);
                classes.put(name, forName);
                classes.put(fullClassName, forName);
                res = forName;
            } catch (final Throwable e) {
                Utils.debug(System.err, e, fullClassName, " class loading failure:");
            }
        return res;
    }

    /**
     * @param name2
     * @return
     */
    private URL getResource(final String name2) {
        return Utils.tryToGetResource(name2, new Class[] { String.class, getClass() }, classLoader);
    }

    public static class classLoader extends URLClassLoader {

        public void addURL(final URL url) {
            super.addURL(url);
        }

        public classLoader(final URL[] urls) {
            super(urls);
        }

        public classLoader(final URL[] urls, final ClassLoader parent, final URLStreamHandlerFactory factory) {
            super(urls, parent, factory);
        }

        public classLoader(final URL[] urls, final ClassLoader parent) {
            super(urls, parent);
        }

    }

    public void addURL(final URL url) {
        final ClassLoader obj = ClassLoader.getSystemClassLoader();
        if (obj instanceof URLClassLoader)
            try {
                final Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
                if (!method.isAccessible())
                    method.setAccessible(true);
                method.invoke(obj, new Object[] { url });
                return;
            } catch (final SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        classLoader.addURL(url);
    }
}
