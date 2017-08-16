/**
 * 
 */
package am.englet.link;

/**
 * @author 1
 * @deprecated
 */
public class SimpleLinkFactory implements LinkFactory {
    private final Storage storage = new StorageImpl();
    private final BackUsageStrategy backUsageStrategy;
    private final BackAdapter backAdapter;

    public SimpleLinkFactory(final BackAdapter backAdapter, final BackUsageStrategy backUsageStrategy) {
        super();
        this.backAdapter = backAdapter;
        this.backUsageStrategy = backUsageStrategy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.link.LinkFactory#instance()
     */
    public Link instance() {
        return backUsageStrategy.tryNext(backAdapter, storage) ? new BackedLink(backAdapter, backUsageStrategy, storage)
                : null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.link.LinkFactory#meta()
     */
    public Object meta() {
        return backAdapter.metadata();
    }

}
