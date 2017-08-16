package am.englet.link;

/**
 * Handles backing iterable Objects for BackedLinks. BackUsageStrategy is just
 * service object and should contain absolutely no data in itself (like
 * BackAdapter does too, except the backing object itself). For intermediary
 * data, an instance of Storage interface is provided. This instance is
 * contained in BackedLinks within a special field.
 * 
 * @author 1
 * 
 */
public interface BackUsageStrategy {
    public Object getCurrent(BackAdapter backAdapter, Storage storage);

    public boolean tryNext(BackAdapter backAdapter, Storage storage);

    public boolean needsStorage();
}
