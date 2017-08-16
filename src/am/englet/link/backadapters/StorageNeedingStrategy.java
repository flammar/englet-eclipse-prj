package am.englet.link.backadapters;

import am.englet.link.BackAdapter;
import am.englet.link.BackUsageStrategy;
import am.englet.link.Storage;

public abstract class StorageNeedingStrategy implements BackUsageStrategy {

    public StorageNeedingStrategy() {
        super();
    }

    protected abstract Object getContent(final BackAdapter backAdapter);

    public final boolean needsStorage() {
        return true;
    }

    public Object getCurrent(final BackAdapter backAdapter,
            final Storage storage) {
        return storage.restore();
    }

    public boolean tryNext(final BackAdapter backAdapter, final Storage storage) {
        if (!preCheckNext(backAdapter))
            return false;
        storage.store(getContent(backAdapter));
        return postCheckNext(storage);
    }

    protected abstract boolean postCheckNext(Storage storage);

    protected abstract boolean preCheckNext(BackAdapter backAdapter);

}