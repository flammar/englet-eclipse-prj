package am.englet.link.backadapters.slider;

import am.englet.util.Checker;

public class FilterSlider implements Slider {
    protected final Checker c;
    protected final Slider s;
    protected transient Object content;

    /**
     * @param s
     * @param c
     */
    public FilterSlider(final Slider s, final Checker c) {
        this.s = s;
        this.c = c;
    }

    public Object content() {
        return content;
    }

    public boolean tryNext() {
        while (true) {
            if (!s.tryNext())
                return false;
            final Object content2 = s.content();
            if (c.check(content2)) {
                content = content2;
                return true;
            }
        }
    }

}
