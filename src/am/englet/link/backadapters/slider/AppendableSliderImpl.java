/**
 * 01.12.2009
 * 
 * 1
 * 
 */
package am.englet.link.backadapters.slider;

import java.util.LinkedList;
import java.util.List;

/**
 * @author 1
 * 
 */
public class AppendableSliderImpl implements AppendableSlider {

    final protected List sliders = new LinkedList();

    public AppendableSliderImpl() {
    }

    public AppendableSliderImpl(final Slider slider) {
        go(slider);
    }

    public void append(final Slider slider) {
        sliders.add(slider);
    }

    public Object content() {
        return content0();
    }

    protected Object content0() {
        return first().content();
    }

    private Slider first() {
        return ((Slider) sliders.get(0));
    }

    public boolean tryNext() {
        while (!(sliders.isEmpty() || tryNext0()))
            sliders.remove(0);
        return !sliders.isEmpty();
    }

    protected boolean tryNext0() {
        return first().tryNext();
    }

    protected void go(final Slider slider) {
        sliders.clear();
        sliders.add(slider);
    }

}