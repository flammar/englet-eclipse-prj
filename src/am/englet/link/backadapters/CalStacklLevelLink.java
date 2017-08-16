/**
 * 
 */
package am.englet.link.backadapters;

import am.englet.link.Link;
import am.englet.link.SliderLink;

/**
 * @author 1
 * 
 */
public class CalStacklLevelLink extends SliderLink {
    protected Link base;

    public CalStacklLevelLink(final Link link) {
        super(link);
        base = link;
    }

    public void go(final Link link) {
        this.link = link;
    }

    public Link getBase() {
        return base;
    }

}
