/**
 * 
 */
package am.englet.link;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 1
 * 
 */
public class AdapterMetadata {
    public final Class backClass;
    public final Set   preferredStrategies;

    public AdapterMetadata(final Class backClass,
            final Class[] preferredStrategies) {
        super();
        this.backClass = backClass;
        final HashSet set = new HashSet(Arrays.asList(preferredStrategies));
        this.preferredStrategies = Collections.unmodifiableSet(set);
    }
}
