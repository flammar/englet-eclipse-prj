package am.englet.link;

public class TrivialBaseBackAdapterImpl implements BackAdapter {
    protected Object back;

    public TrivialBaseBackAdapterImpl() {
        super();
    }

    public TrivialBaseBackAdapterImpl(final Object back) {
        super();
        setBack(back);
    }

    public Object current() {
        throw new UnsupportedOperationException();
    }

    public Object getNext() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
        throw new UnsupportedOperationException();
    }

    public boolean tryNext() {
        throw new UnsupportedOperationException();
    }

    public Object metadata() {
        return null;
    }

    public void setBack(final Object o) {
        if (back == null)
            back = o;
    }
}
