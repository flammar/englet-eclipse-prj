/**
 * 26.10.2009
 *
 * 1
 *
 */
package am.englet.bsh;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import am.englet.MethodsStorage;
import am.englet.SingletonPool;
import am.englet.Utils;
import am.englet.MethodsStorage.MethodRecord;
import bsh.BshMethod;
import bsh.Interpreter;

/**
 * @author 1
 * 
 */
public class Management {

    public static void adapt_bsh_script(final String fileName,
            final MethodsStorage ms, final SingletonPool pool) throws Exception {
        final Interpreter interpreter = (Interpreter) pool
                .getSingleton(Interpreter.class);
        // script must return object of one on following types
        final Object result = interpreter.source(fileName);
        // methods array and METHOD_NAME_REPLACEMENTS String
        List result2;
        if ((result instanceof List) && ((result2 = (List) result).size() == 2)) {
            final boolean b = result2.get(0) instanceof String;
            putProbablyReplaced(ms, (BshMethod[]) result2.get(b ? 1 : 0),
                    (String) result2.get(b ? 0 : 1));
            // methods array and no METHOD_NAME_REPLACEMENTS String
        } else if (result instanceof BshMethod[])
            putProbablyReplaced(ms, (BshMethod[]) result, "");
        // methods Map with already replaced name keys
        else if (result instanceof Map) {
            final Map result3 = (Map) result;
            for (final Iterator i = result3.keySet().iterator(); i.hasNext();) {
                final String key = i.next().toString();
                final BshMethod method = (BshMethod) result3.get(key);
                adaptMethod(ms, method, key);
            }
        }

    }

    private static void putProbablyReplaced(final MethodsStorage ms,
            final BshMethod[] methods, final String methodNameReplacements) {
        final HashMap replacement = new HashMap();
        final List l = Collections.list(new StringTokenizer(
                methodNameReplacements));
        for (int i = 0; i < l.size(); replacement.put(l.get(i++), l.get(i++)))
            ;
        for (int i = 0; i < methods.length; i++) {
            final BshMethod m = methods[i];
            final String mn = m.getName();
            final Object ma = replacement.get(mn);
            final String key = ma == null ? mn : ma.toString();
            Utils.debug(System.out, "m=", m);
            adaptMethod(ms, m, key);
        }
    }

    /**
     * @param ms
     * @param m
     * @param key
     */
    private static void adaptMethod(final MethodsStorage ms, final BshMethod m,
            final String key) {
        ms.put(key, new MethodRecord(new MethodInvokable /* Metadata */(m),
                MethodRecord.Type.PROCESSING));
    }
}
