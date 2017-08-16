/**
 * 19.11.2009
 *
 * 1
 *
 */
package am.englet.dispatch;

import java.util.Arrays;

import am.englet.CastingContext;
import am.englet.CodeBlock;
import am.englet.MethodsStorage;
import am.englet.MethodsStorage.MethodRecord;

/**
 * @author 1
 * 
 */
public class Utils {
    public static MethodsStorage.MethodRecord search(
            final MethodsStorage.MethodRecord[] where/*
                                                      * final Class[][] where
                                                      */, final Class[] what,
            final CastingContext cont, final boolean simpleSingle) {
        am.englet.Utils.debug(null, new CodeBlock() {

            public Object result() {
                return new Object[] { "search: what: ", Arrays.asList(what) };
            }
        });
        if ((where.length < 2) && simpleSingle) {
            final MethodRecord methodRecord = where[0];
            am.englet.Utils.debug(System.out, new Object[] {
                    "search: to return: unique: ", methodRecord });
            return methodRecord;
        }
        for (int i = 0; i < where.length; i++) {
            final Class[] classes = where[i].getParameterTypes();
            if (what.length < classes.length)
                continue;
            well: {
                final int i1 = i;
                am.englet.Utils.debug(null, new CodeBlock() {

                    public Object result() {
                        return new Object[] { "search: where[",
                                new Integer(i1), "].getParameterTypes():",
                                Arrays.asList(classes) };
                    }
                });
                for (int j = what.length, k = classes.length; (j-- > 0)
                        && (k-- > 0);)
                    // it's prov's duty to properly handle case of
                    // target.isAssignableFrom(source) == true
                    if (!cont.canCast(classes[k], what[j]))
                        break well;
                am.englet.Utils.debug(System.out, "search: to return: ",
                        where[i]);
                return where[i];
            }
        }
        am.englet.Utils.debug(System.out, "search: not found: ",
                new CodeBlock() {

                    public Object result() {
                        return (what != null ? Arrays.asList(what).toString()
                                : null);
                    }
                });
        return null;
    }
}
