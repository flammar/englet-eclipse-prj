package am.englet;

import java.io.FileReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import net.sourceforge.yamlbeans.YamlReader;
import am.englet.MethodsStorage.MethodRecord;

public class YAMLBasedEngletSettings implements EngletSettings {

    public void apply(final Englet englet) throws Exception {
        final String setsFile = System.getProperty("englet.settings.file",
                "englet.settings.yml");
        final YamlReader reader = new YamlReader(new FileReader(setsFile));
        final Map sets = (Map) reader.read();
        Englet.debug = Boolean.valueOf((String) sets.get("debug"))
                .booleanValue();
        processInvokables(englet, sets.get("invokables"), new Performer() {

            public void perform(final Englet englet, final String name,
                    final Object value) {
                processInvValue(englet, name, value);
            }
        });
        processInvokables(englet, sets.get("compound-invokables"),
                new Performer() {

                    public void perform(final Englet englet, final String name,
                            final Object value) {

                        try {
                            parse(englet, value);
                            final Invokable pop = (Invokable) englet.getStack()
                                    .pop();
                            englet.getMethods().put(
                                    name,
                                    new MethodRecord(pop,
                                            MethodRecord.Type.PROCESSING));
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
        englet.getStack().clear();
    }

    /**
     * @param englet
     * @param value
     * @throws Exception
     */
    private static void parse(final Englet englet, final Object value)
            throws Exception {
        englet.parse((String) value);
    }

    /**
     * @param englet
     * @param invokables
     * @param p
     */
    private void processInvokables(final Englet englet,
            final Object invokables, final Performer p) {
        if (invokables instanceof List)
            for (final Iterator iterator = ((List) invokables).iterator(); iterator
                    .hasNext();)
                processInvMap(englet, (Map) iterator.next(), p);
        else
            processInvMap(englet, (Map) invokables, p);
    }

    /**
     * @param englet
     * @param m1
     * @param p
     */
    private void processInvMap(final Englet englet, final Map m1,
            final Performer p) {
        for (final Iterator iterator2 = m1.entrySet().iterator(); iterator2
                .hasNext();) {
            final Entry entry = (Entry) iterator2.next();
            p.perform(englet, (String) entry.getKey(), entry.getValue());
        }
    }

    private void processInvValue(final Englet englet, final String name,
            final Object value) {
        if (value instanceof Map)
            adapt_method(englet, name, (String) ((Map) value).get("class"),
                    (String) ((Map) value).get("mask"));
        else if (value instanceof List) {
            final List l = (List) value;
            adapt_method(englet, name, (String) l.get(0),
                    l.size() > 1 ? (String) l.get(1) : name);
        } else /* String */{
            final String str = (String) value;
            final StringTokenizer st = new StringTokenizer(str.substring(1),
                    str.substring(0, 1));
            adapt_method(englet, name, st.nextToken(), st.hasMoreTokens() ? st
                    .nextToken() : name);
        }
        /*
         * if(value inst)
         */

    }

    private void adapt_method(final Englet englet, final String key,
            final String className, final String mask) {
        final Class forName = getClass(className);
        if (forName != null)
            Management.adapt_method(englet.getMethods(), forName, mask, key);
    }

    /**
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    private Class getClass(final String className) {
        try {
            return Class.forName(className);
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    interface Performer {
        void perform(final Englet englet, final String name, final Object value);
    }

}
