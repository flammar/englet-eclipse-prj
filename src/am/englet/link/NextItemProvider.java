/**
 * 01.03.2010
 *
 * 1
 *
 */
package am.englet.link;

import java.io.Serializable;

public class NextItemProvider implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final Storage storage /* = new StorageImpl() */;
    private final BackUsageStrategy backUsageStrategy;
    private final BackAdapter backAdapter;

    /**
     * @param backUsageStrategy
     * @param backAdapter
     */
    public NextItemProvider(final BackUsageStrategy backUsageStrategy, final BackAdapter backAdapter) {
        storage = backUsageStrategy.needsStorage() ? new StorageImpl() : null;
        this.backUsageStrategy = backUsageStrategy;
        this.backAdapter = backAdapter;
    }

    public boolean tryNext() {
        return backUsageStrategy.tryNext(backAdapter, storage);
    }

    public Object content() {
        return backUsageStrategy.getCurrent(backAdapter, storage);
    }

}