/**
 * 22.10.2009
 * 
 * 1
 * 
 */
package am.englet.link;

import java.util.List;

/**
 * @author 1
 * 
 */
public class LinkUtils {

    public static FinalLink ListAsFinalLinkChain(final List l) {
        FinalLink res = null;
        for (int i = l.size(); i-- > 0; res = new FinalLink(l.get(i), res))
            ;
        return res;
    }

}
