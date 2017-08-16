package am.englet.lookup;

public class LinkToCollectionConverterSource implements InvokableCandidateSource {

    public InvokableCandidate getInstance(final LookupContext context) {
        return check(context) ? find(context) : null;
    }

    private boolean check(final LookupContext context) {
        // TODO Auto-generated method stub
        return false;
    }

    private InvokableCandidate find(final LookupContext context) {
        // TODO Auto-generated method stub
        return null;
    }

}
