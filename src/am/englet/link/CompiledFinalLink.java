package am.englet.link;

/**
 * The Class CompiledFinalLink. Linj fen Zal om najT k'juv FinalLink jav
 * 'Trace-emT.
 */
public class CompiledFinalLink extends FinalLink {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -672932755238838171L;

    /** The offset. */
    private int offset = -1;

    /**
     * Instantiates a new compiled final link.
     * 
     * @param content
     *            the content
     * @param next
     *            the next
     * @param offset
     *            the offset
     */
    public CompiledFinalLink(final Object content, final FinalLink next,
            final long offset) {
        super(content, next);
        this.offset = (int) (offset & 0xffffffff);
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.link.FinalLink#toString()
     */
    public String toString() {
        return " @"
                + offset
                + " "
                + (content instanceof FinalLink ? "[" + content + "]" : ""
                        + content) + (next == null ? "" : " || " + next);
    }

}
