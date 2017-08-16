/**
 * 20.10.2009
 * 
 * 1
 * 
 */
package am.englet.link.backadapters.slider;

import am.englet.link.Link;

/**
 * CallStack interface. The number in method arguments means the number of stack
 * level starting from the stack top, i.e. "0" means stack top (= stack.peek()),
 * "1" means next down from stack top (= stack.get(stack.size()-2)), "2" means
 * third down starting from stack top (= stack.get(stack.size()-3)), and so on
 * 
 * @author 1
 * 
 */
public interface CallStackSlider extends Slider {

    /**
     * at the n-th level, set the given link as the link to start sliding
     * through
     * 
     * @param link
     * @param n
     */
    public void go(Link link, int n);

    /**
     * return the link that was the first through which sliding was started at
     * the n-th level
     * 
     * @param n
     * @return
     */
    public Link current(int n);

    /**
     * create new stack level based on the link to start sliding through
     * 
     * @param link
     */
    public void start(Link link);

    /**
     * Drop from call stack all levels till the n-th inclusively, continue
     * sliding from the (n+1)-th level
     * 
     * @param n
     */
    public void drop(int n);
}
