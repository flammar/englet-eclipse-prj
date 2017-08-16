package am.englet.lookup;

import java.util.Properties;

public interface InvokableCandidateSourceFactory {
    InvokableCandidateSource getInstance(Properties pr);
}
