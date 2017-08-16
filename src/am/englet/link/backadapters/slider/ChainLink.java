/**
 * 
 */
package am.englet.link.backadapters.slider;

import java.util.Stack;

import am.englet.link.AbstractLink;
import am.englet.link.Link;
import am.englet.link.SliderLink;

/**
 * "Stack" of SliderLink objects.
 * 
 * @author 1
 * 
 */
public class ChainLink extends AbstractLink {
    private final Stack links = new Stack();

    public Object push(final SliderLink item) {
        return links.push(item);
    }

    public void drop(final int n) {
        links.setSize(links.size() - n - 1);
    }

    public SliderLink at(final int n) {
        return (SliderLink) links.get(links.size() - n - 1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.link.Link#content()
     */
    public Object content() {
        return peek().content();
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.link.Link#next()
     */
    public Link next() {
        while (peek().next() == null && links.size() > 0)
            links.pop();
        return links.size() > 0 ? this : null;
    }

    private Link peek() {
        return (Link) links.peek();
    }

}
