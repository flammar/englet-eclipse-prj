/**
 * 
 */
package am.englet.link.backadapters;

import am.englet.link.BackAdapter;

/**
 * unsafe
 * 
 * @author 1
 * 
 */
public abstract class CurrentlessStrategy extends StorageNeedingStrategy {
    /**
     * @param backAdapter
     * @return
     */
    protected Object getContent(final BackAdapter backAdapter) {
        return backAdapter.getNext();
    }

}
