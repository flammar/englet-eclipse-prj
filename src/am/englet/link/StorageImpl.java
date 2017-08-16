/**
 *
 */
package am.englet.link;

import java.io.Serializable;

/**
 * @author 1
 * 
 */
public class StorageImpl implements Storage, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6224951365762775000L;
    private Object object = Storage.NOTHING;

    /*
     * (non-Javadoc)
     * 
     * @see am.link.Storage#store(java.lang.Object)
     */
    public void store(final Object object) {
        this.object = object;
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.link.Storage#restore()
     */
    public Object restore() {
        if (object != null && object.equals(Storage.NOTHING))
            throw new IllegalStateException("Storage is marked as empty.");
        return object;
    }

}
