package am.englet.link;

import java.io.Serializable;

public class LazyLink implements Link, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1233674093359903978L;
    final Object content;
    private LazyLink next = null;
    NextItemProvider nextItemProvider;

    /**
     * @param nextItemProvider
     * @param content
     */
    public LazyLink(final NextItemProvider nextItemProvider,
            final Object content) {
        this.nextItemProvider = nextItemProvider;
        this.content = content
        // instanceof Link && !(content instanceof FinalLink)?
        // :
        ;
    }

    /**
     * Not safe. Implies that it is possible to call nextItemProvider.content()
     * .
     * 
     * @param nextItemProvider
     * @return
     */
    public LazyLink(final NextItemProvider nextItemProvider) {
        this(nextItemProvider, nextItemProvider.content());
    }

    public Object content() {
        return content;
    }

    public Link next() {
        if (nextItemProvider == null)
            return next;
        else {
            final boolean tryNext = nextItemProvider.tryNext();
            if (!tryNext)
                return null;
            else {
                final NextItemProvider nextItemProvider2 = nextItemProvider;
                next = new LazyLink(nextItemProvider2);
                nextItemProvider = null;
                return next();
            }
        }
    }

}
