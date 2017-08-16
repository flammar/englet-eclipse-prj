package am.englet.util;

import java.util.AbstractList;

public class ProviderBasedUnmodifiableList extends AbstractList {

    UnmodifiableListDataProvider provider;

    public ProviderBasedUnmodifiableList(final UnmodifiableListDataProvider provider) {
        super();
        this.provider = provider;
    }

    public Object get(final int index) {
        return provider.get(index);
    }

    public int size() {
        return provider.size();
    }

}
