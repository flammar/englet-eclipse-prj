package am.englet.lookup.contextconverters;

import am.englet.Utils;
import am.englet.lookup.Candidate;
import am.englet.lookup.CandidateConverter;
import am.englet.lookup.Lookup;
import am.englet.lookup.LookupContext;

public class MethodNonBasic implements CandidateConverter {

    public Object convert(final Candidate lookupContext) {
        final LookupContext lookupContext2 = lookupContext.getInitialLookupContext();
        Utils.debug(System.out, "MethodNonBasic.convert():", lookupContext2.command);
        final boolean lookUpMethodNonBasic = Lookup.lookupMethodNonBasic(lookupContext2);
        return Boolean.valueOf(lookUpMethodNonBasic);
    }

    public Object describeFail(final LookupContext lookupContext) {
        return Utils.simpleName(getClass()) + ": Looking for " + lookupContext.command + " failed";
    }
}
