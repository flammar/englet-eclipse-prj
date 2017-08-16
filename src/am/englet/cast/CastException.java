/**
 * 28.10.2009
 * 
 * 1
 * 
 */
package am.englet.cast;

/**
 * @author 1
 * 
 */
public class CastException extends RuntimeException {

    private static final long serialVersionUID = -185561752752896956L;

    /**
     * 
     */
    public CastException() {
        super();
    }

    /**
     * @param message
     */
    public CastException(final String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public CastException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public CastException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
