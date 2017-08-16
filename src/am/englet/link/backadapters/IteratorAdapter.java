package am.englet.link.backadapters;

import java.util.Iterator;

import am.englet.link.AdapterMetadata;
import am.englet.link.TrivialBaseBackAdapterImpl;

public class IteratorAdapter extends TrivialBaseBackAdapterImpl {

    private final static AdapterMetadata metadata = new AdapterMetadata(
                                                          Iterator.class,
                                                          new Class[] { IteratorStrategy.class });

    public IteratorAdapter(final Iterator back) {
        super(back);
    }

    public IteratorAdapter() {
        super();
    }

    public boolean hasNext() {
        return getBack().hasNext();
    }

    public Object getNext() {
        return getBack().next();
    }

    public Object metadata() {
        Iterator back2 = getBack();
        return back2 == null ? (Object) metadata : back2;
    }

    protected Iterator getBack() {
        return (Iterator) back;
    }

}
