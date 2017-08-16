/**
 * 
 */
package am.englet.link.backadapters.slider;

import am.englet.link.FinalLink;
import am.englet.link.Link;

/**
 * @author 1
 * 
 */
public class LinkSlider implements Slider {

    private Link link;

    public LinkSlider(final Link link) {
        // if (link == null)
        // throw new IllegalArgumentException("null proposed as back");
        go(link);
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.link.backadapters.slider.Slider#content()
     */
    public Object content() {
        if (link == null)
            throw new IllegalStateException("Back object exhausted");
        final Object content = link.content();
        // System.out.println("LinkSlider.content():" + getClass().getName()
        // + ".content(): " + content);
        return content;
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.link.backadapters.slider.Slider#tryNext()
     */
    public boolean tryNext() {
        return (link != null) && ((link = link.next()) != null);
    }

    /**
     * Set new FinalLink(link, link) as current Link to be able to start with
     * tryNext(), so first, newly created FinalLink is to be lost.
     * 
     * @param link
     */
    protected void go(final Link link) {
        this.link = new FinalLink(link, link);
    }

}
