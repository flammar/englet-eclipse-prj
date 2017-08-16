/**
 *
 */
package am.englet.link.backadapters;

import am.englet.link.BackAdapter;
import am.englet.link.Storage;

/**
 * @author Adm1
 * 
 */
public class CurrentOnceStrategy extends StorageNeedingStrategy {

    /*
     * (non-Javadoc)
     * 
     * @see
     * am.englet.link.backadapters.CurrentlessStrategy#postCheckNext(am.englet
     * .link.Storage)
     */
    protected boolean postCheckNext(final Storage storage) {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * am.englet.link.backadapters.CurrentlessStrategy#preCheckNext(am.englet
     * .link.BackAdapter)
     */
    protected boolean preCheckNext(final BackAdapter backAdapter) {
        return backAdapter.tryNext();
    }

    protected Object getContent(final BackAdapter backAdapter) {
        return backAdapter.current();
    }

}
