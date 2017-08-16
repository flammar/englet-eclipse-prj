package am.englet.lookup;

public interface CandidateConverter {
    Object convert(Candidate candidate);

    Object describeFail(LookupContext lookupContext);

}
