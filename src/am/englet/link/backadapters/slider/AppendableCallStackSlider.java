package am.englet.link.backadapters.slider;

import am.englet.link.Link;

public interface AppendableCallStackSlider extends CallStackSlider,
		AppendableSlider {

	public Link current(final int n);

	public void drop(final int n);

	public void go(final Link link, final int n);

	public void start(final Link link);

	public void append(final Link link, final int n);

	public Object content();

	public boolean tryNext();

}
