/**
 * 
 */
package am.englet.link;

/**
 * "Proxy" object for link object.
 * 
 * @author 1
 * 
 */
public class SliderLink extends AbstractLink {

    protected Link link;

    public SliderLink(final Link link) {
        super();
        this.link = link;
    }

    public Object content() {
        return link.content();
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.link.Link#next()
     */
    public Link next() {
        link = link.next();
        return link == null ? null : this;
    }

}
