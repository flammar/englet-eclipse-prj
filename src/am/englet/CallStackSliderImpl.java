package am.englet;

import java.util.Stack;

import am.englet.link.Link;
import am.englet.link.backadapters.slider.AppendableBackHoldingLinkSlider;
import am.englet.link.backadapters.slider.AppendableCallStackSlider;
import am.englet.link.backadapters.slider.AppendableSlider;
import am.englet.link.backadapters.slider.CallStacklLevelSlider;
import am.englet.link.backadapters.slider.LinkSlider;
import am.englet.link.backadapters.slider.Slider;

/**
 * @author 1
 * 
 */
public class CallStackSliderImpl implements AppendableCallStackSlider {

    private final Stack stack = new Stack();

    protected CallStacklLevelSlider peek() {
        return (CallStacklLevelSlider) stack.peek();
    }

    protected CallStacklLevelSlider at(final int index) {
        return (CallStacklLevelSlider) stack.get(realIndex(index));
    }

    private int realIndex(final int index) {
        return (index >= 0 ? stack.size() : 0) - 1 - index;
    }

    public void drop(final int n) {
        stack.setSize(realIndex(n));
    }

    public Link current(final int n) {
        return at(n).back();
    }

    public void go(final Link link, final int n) {
        at(n).go(link);
    }

    public void start(final Link link) {
        stack.push(new AppendableBackHoldingLinkSlider(link));
    }

    public boolean tryNext() {
        // System.out.println("tryNext");
        // System.out.println("stack.size()=" + stack.size());
        while ((stack.size() > 0) && !peek().tryNext())
            stack.pop();
        final boolean b = stack.size() > 0;
        // System.out.println("tryNext: " + b);
        return b;
    }

    public Object content() {
        return peek().content();
    }

    public void append(final Link link, final int n) {
        ensureStarted();
        ((AppendableSlider) at(n)).append(new LinkSlider(link));
    }

    private void ensureStarted() {
        if (stack.size() == 0)
            start(null);
    }

    public void append(final Slider slider) {
        ensureStarted();
        ((AppendableSlider) stack.get(0)).append(slider);

    }

    protected Stack getStack() {
        return stack;
    }
}