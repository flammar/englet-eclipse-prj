/**
 * 
 */
package am.englet.link.backadapters.slider;

import am.englet.link.AdapterMetadata;
import am.englet.link.TrivialBaseBackAdapterImpl;
import am.englet.link.backadapters.ResultSetStrategy;

/**
 * @author 1
 * 
 */
public class LinkSliderAdapter extends TrivialBaseBackAdapterImpl {

    private final static AdapterMetadata metadata = new AdapterMetadata(
            Slider.class, new Class[] { ResultSetStrategy.class });

    public LinkSliderAdapter(final Slider slider) {
        super(slider);
    }

    public Object current() {
        return getSlider().content();
    }

    public boolean tryNext() {
        return getSlider().tryNext();
    }

    private Slider getSlider() {
        return (Slider) back;
    }

    public Object metadata() {
        final Slider back2 = getSlider();
        return back2 == null ? (Slider) metadata : back2;
    }

}
