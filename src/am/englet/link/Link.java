/**
 * 
 */
package am.englet.link;

/**
 * @author 1
 * 
 */
public interface Link {
    public Link next();

    public Object content();

    public interface Serializable extends Link, java.io.Serializable {
    }
}
