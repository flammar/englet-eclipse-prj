/**
 * 
 */
package am.englet.link.backadapters;

import am.englet.link.BackAdapter;
import am.englet.link.Storage;

/**
 * @author 1
 * 
 */
public class IteratorStrategy extends CurrentlessStrategy {
    protected boolean postCheckNext(final Storage storage) {
        return true;
    }

    protected boolean preCheckNext(final BackAdapter backAdapter) {
        return backAdapter.hasNext();
    }

    public final static IteratorStrategy INSTANCE = new IteratorStrategy();
}
