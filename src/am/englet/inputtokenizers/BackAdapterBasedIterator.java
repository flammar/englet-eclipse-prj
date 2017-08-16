package am.englet.inputtokenizers;

import java.util.Iterator;

import am.englet.link.BackAdapter;
import am.englet.link.BackUsageStrategy;
import am.englet.link.Storage;
import am.englet.link.StorageImpl;

public class BackAdapterBasedIterator implements Iterator {
    private final BackAdapter backAdapter;
    private final BackUsageStrategy strategy;
    private final Storage storage;

    /**
     * @param backAdapter
     * @param strategy
     */
    public BackAdapterBasedIterator(final BackAdapter backAdapter,
            final BackUsageStrategy strategy) {
        this.backAdapter = backAdapter;
        this.strategy = strategy;
        storage = strategy.needsStorage() ? new StorageImpl() : null;
    }

    boolean nextTried = false, hasNext = true;

    public boolean hasNext() {
        // System.out.println("BackAdapterBasedIterator.hasNext():nextTried:"
        // + nextTried);
        if (nextTried)
            return hasNext;
        final boolean tryNext = tryNext();
        hasNext = tryNext;
        nextTried = true;
        // System.out.println("BackAdapterBasedIterator.hasNext():tryNext:"
        // + tryNext);
        return tryNext;
    }

    /**
     * @return
     */
    private boolean tryNext() {
        return strategy.tryNext(backAdapter, storage);
    }

    public Object next() {
        if (nextTried)
            nextTried = false;
        else if (!tryNext())
            throw new IllegalStateException("Back exhausted.");
        return strategy.getCurrent(backAdapter, storage);
    }

    public void remove() {
    }

}
