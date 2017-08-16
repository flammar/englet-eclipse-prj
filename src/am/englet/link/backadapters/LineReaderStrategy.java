/**
 * 
 */
package am.englet.link.backadapters;

import am.englet.link.BackAdapter;
import am.englet.link.Storage;

/**
 * unsafe
 * 
 * @author 1
 * 
 */
public class LineReaderStrategy extends CurrentlessStrategy {
    protected boolean postCheckNext(final Storage storage) {
        return storage.restore() != null;
    }

    protected boolean preCheckNext(final BackAdapter backAdapter) {
        return true;
    }

}
