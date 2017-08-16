package am.englet.lookup.contextconverters;

import am.englet.Utils;
import am.englet.lookup.Candidate;
import am.englet.lookup.CandidateConverter;
import am.englet.lookup.Lookup;
import am.englet.lookup.LookupContext;

public class MethodBasic implements CandidateConverter {

    public Object describeFail(final LookupContext lookupContext) {
        return Utils.simpleName(getClass()) + ": Looking for " + lookupContext.command + " failed";
    }

    public Object convert(final Candidate lookupContext) {
        final LookupContext lookupContext1 = lookupContext.getInitialLookupContext();
        Utils.debug(System.out, "MethodBasic.convert():", lookupContext1.command);
        final boolean lookUpMethodBasic = Lookup.lookUpMethodBasic(lookupContext1);
        return Boolean.valueOf(lookUpMethodBasic);
    }
}
