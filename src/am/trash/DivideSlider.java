/**
 * 30.11.2009
 * 
 * 1
 * 
 */
package am.trash;

import am.englet.link.FinalLink;
import am.englet.link.Link;
import am.englet.link.backadapters.slider.LinkSlider;

/**
 * @author 1
 * 
 */
public class DivideSlider extends LinkSlider {
	private Link res;
	private final int quotient;

	/**
	 * @param link
	 */
	public DivideSlider(final Link link, final int quotient) {
		super(link);
		this.quotient = quotient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see am.englet.link.backadapters.slider.LinkSlider#tryNext()
	 */
	public boolean tryNext() {
		Link l = null;
		res = null;
		int n = quotient;
		while ((n-- > 0) && super.tryNext())
			l = new FinalLink(super.content(), l);
		// if not enough then return false and discard rest
		final boolean b = n < 0;
		if (b)
			for (; l != null; l = l.next())
				res = new FinalLink(l.content(), res);
		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see am.englet.link.backadapters.slider.LinkSlider#content()
	 */
	public Object content() {
		return res;
	}

}
