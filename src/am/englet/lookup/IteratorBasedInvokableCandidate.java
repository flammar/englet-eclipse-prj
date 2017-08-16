package am.englet.lookup;

import java.util.Iterator;

import am.englet.Invokable;

public class IteratorBasedInvokableCandidate implements InvokableCandidate {
    private final Iterator iterator;

    public IteratorBasedInvokableCandidate(final Iterator iterator) {
        this.iterator = iterator;
    }

    public Invokable getRealisation() {
        while (iterator.hasNext()) {
            final Object next = iterator.next();
            if (next instanceof InvokableCandidate) {
                final Invokable realisation = ((InvokableCandidate) next).getRealisation();
                if (realisation != null)
                    return realisation;
            }
        }
        return null;
    }

}
