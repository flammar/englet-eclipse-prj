package am.englet.link;

/**
 * Adapter for backing iterable Object. BackAdapter is just service object and
 * should not contain any data except this backing object (e.g. intermediary
 * objects for storing data), so BackAdapter should be just method calls
 * delegator. Intermediary objects for storing data are instances of Storage
 * interface and are to be held by objects that use the adapter and handled by
 * BackUsageStrategy objects (which, in turn, should contain absolutely no data
 * in themselves).
 * 
 * Implementors should have 2 constructors: 1 accepting the back object and 1
 * without arguments for further using setBack() method, usually within factory.
 * The one-argument constructor shall be used for reflectional analysis within
 * factory to indicate back object class it matches.
 * 
 * Also metadata() method should return adapter metadata if no back object is
 * set.
 * 
 * @author 1
 * 
 */
public interface BackAdapter {
    public abstract Object current();

    public abstract Object getNext();

    public abstract boolean hasNext();

    public abstract boolean tryNext();

    public abstract void setBack(Object back);

    /**
     * @return back object metadata if back object is already set, otherwise
     *         adapter itself's metadata
     */
    public abstract Object metadata();

}