/**
 * 01.12.2009
 * 
 * 1
 * 
 */
package am.englet.link.backadapters.slider;

import java.util.LinkedList;
import java.util.List;

import am.englet.link.Link;

/**
 * @author 1
 * 
 */
public class AppendableBackHoldingLinkSlider extends AppendableSliderImpl
        implements CallStacklLevelSlider {

    final protected Link back;
    protected final List slided = new LinkedList();
    protected boolean fresh;

    public AppendableBackHoldingLinkSlider(final Link link) {
        super(new LinkSlider(link));
        back = link;
    }

    public Link back() {
        return back;
    }

    public void go(final Link link) {
        go(new LinkSlider(link));
    }

    public Object content() {
        final Object res = content0();
        if (fresh) {
            slided.add(res instanceof Link ? "<<<Link>>>" : res);
            fresh = false;
        }
        return res;
    }

    public boolean tryNext() {
        while (!(sliders.isEmpty() || tryNext00()))
            sliders.remove(0);
        return !sliders.isEmpty();
    }

    protected boolean tryNext00() {
        final boolean res = tryNext0();
        if (res)
            fresh = true;
        return res;
    }

    public String toString() {
        return "AppendableBackHoldingLinkSlider [slided=" + slided + "]";
    }

}
