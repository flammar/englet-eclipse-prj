package am.englet.lookup;

import java.util.Iterator;

public class IteratorBasedInvokableCandidateSource implements InvokableCandidateSource {
    private final Iterator iterator;

    public IteratorBasedInvokableCandidateSource(final Iterator invokableCandidateSourceIterator, final LookupContext lookupContext) {
        this.iterator = invokableCandidateSourceIterator;
    }

    public InvokableCandidate getInstance(final LookupContext context) {
        final Iterator iterator2 = new Iterator() {

            public void remove() {
            }

            public Object next() {
                final Object next = iterator.next();
                return next instanceof InvokableCandidateSource ? ((InvokableCandidateSource) next)
                        .getInstance(context) : null;
            }

            public boolean hasNext() {
                return iterator.hasNext();
            }
        };
        final IteratorBasedInvokableCandidate iteratorBasedInvokableCandidate = new IteratorBasedInvokableCandidate(
                iterator2);
        return iteratorBasedInvokableCandidate;
    }

}
