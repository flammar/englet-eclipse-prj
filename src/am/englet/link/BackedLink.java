/**
 * 
 */
package am.englet.link;

/**
 * @author 1
 * 
 */
public class BackedLink extends AbstractLink {
    private final Storage storage;
    private final BackUsageStrategy backUsageStrategy;
    private final BackAdapter backAdapter;

    public BackedLink(final BackAdapter backAdapter,
            final BackUsageStrategy backUsageStrategy, final Storage storage) {
        super();
        this.backAdapter = backAdapter;
        this.backUsageStrategy = backUsageStrategy;
        this.storage = storage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.link.Link#content()
     */
    public Object content() {
        return backUsageStrategy.getCurrent(backAdapter, storage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.link.Link#next()
     */
    public Link next() {
        return backUsageStrategy.tryNext(backAdapter, storage) ? this : null;
    }

}
