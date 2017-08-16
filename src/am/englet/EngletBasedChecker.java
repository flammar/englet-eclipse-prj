/**
 * 23.07.2010
 *
 * 1
 *
 */
package am.englet;

import am.englet.link.Link;
import am.englet.util.Checker;

/**
 * @author 1
 * 
 */
public class EngletBasedChecker implements Checker {
    /**
     * @param englet
     * @param link
     */
    public EngletBasedChecker(final Englet englet, final Link link) {
        this.englet = englet;
        this.link = Utils.lazy(link);
    }

    private final Englet englet;
    private final Link link;

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.util.Checker#check(java.lang.Object)
     */
    public boolean check(final Object o) {
        try {
            final Object run = Utils.run(englet, o, link);
            final boolean boolean1 = (Utils.STACK_IS_EMPTY != (run))
                    && Utils.toBoolean(run);
            return boolean1;
        } catch (final Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

}
