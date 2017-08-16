/**
 * 
 */
package am.englet.link;

/**
 * Factory for creating BackedLinks.
 * 
 * @author 1
 * 
 */
public interface LinkFactory {
    public Link instance();

    public Object meta();

}
