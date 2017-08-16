package am.englet.lookup;

import am.englet.Links.ValueConverter;

public class ValueConverterBasedCandidateConverter implements CandidateConverter {
    private final ValueConverter valueConverter;

    public Object convert(final Candidate candidate) {
        return valueConverter.convert(candidate);
    }

    public ValueConverterBasedCandidateConverter(final ValueConverter valueConverter) {
        super();
        this.valueConverter = valueConverter;
    }

    public Object describeFail(final LookupContext lookupContext) {
        return "ValueConverter:" + valueConverter;
    }

}
