package am.englet.link;

import java.util.AbstractList;
import java.util.List;

/**
 * Storage to be used within BackAdapter usage strategy if it needs intermediary
 * storage
 * 
 * @author 1
 * 
 */
public interface Storage {

    /**
     * @author Adm1
     * 
     */
    public static final class Nothing extends AbstractList {
    
        public int size() {
            // TODO Auto-generated method stub
            return 1;
        }
    
        public Object get(final int index) {
            // TODO Auto-generated method stub
            if (index != 0) throw new ArrayIndexOutOfBoundsException(index);
            else return "NOTHING";
        }
    }

    public static final List NOTHING = new Nothing();// Collections.singletonList//Collections.singletonList//

    public Object restore();

    public void store(final Object object);

}