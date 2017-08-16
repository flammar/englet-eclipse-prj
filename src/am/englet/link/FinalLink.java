/**
 * 
 */
package am.englet.link;

import java.io.ObjectStreamException;

/**
 * @author 1
 * 
 */
public class FinalLink extends AbstractLink implements Link.Serializable {

    private static final String CLOSING_BRACKET_SYMBOL = "]";

    private static final String OPENING_BRACKET_SYMBOL = "[";

    public static class Serializable implements /* Link, */java.io.Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = 7900875321579563211L;

        protected Serializable() {
        }

        private Serializable(final FinalLink finalLink) {
            next = finalLink.next;
            content = finalLink.content;
        }

        protected Link next;
        protected Object content;

        protected Object readResolve() throws ObjectStreamException {
            try {
                return new FinalLink(content, next);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static final long serialVersionUID = -2800657836626241993L;

    final/* Final */Link next;
    final Object content;

    {
        rewindable = true;
    }

    public FinalLink(final Object content, /* Final */final Link next) {
        this.next = next;
        this.content = content;
    }

    public FinalLink(final Object content, final FinalLink next) {
        this(content, (Link) next);
    }

    public FinalLink(final Object content) {
        this(content, null);
    }

    // protected FinalLink() {
    // }

    public Object content() {
        return content;
    }

    public Link next() {
        return next;
    }

    public FinalLink prepend(final Object newContent) {
        return new FinalLink(newContent, this);
    }

    protected Object writeReplace() throws ObjectStreamException {
        return new Serializable(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return contentString() + (next == null ? "" : " || " + next);
    }

    private String contentString() {
        final String ob = OPENING_BRACKET_SYMBOL;
        final String cb = CLOSING_BRACKET_SYMBOL;
        return content == null ? ob + cb : (content instanceof FinalLink ? ob
                + content + cb : content.toString());
    }

}
