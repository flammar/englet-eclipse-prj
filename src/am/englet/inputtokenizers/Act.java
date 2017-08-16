/**
 * 01.07.2010
 * 
 * 1
 * 
 */
package am.englet.inputtokenizers;


public class Act extends am.englet.stateengine.Act {
    private final Performer[] performer;
    private final ReaderToTokenizerAdapter readerToTokenizerAdapter;
    public final static int PUSHBACK = -1;
    public final static int APPEND = -2;
    public final static int SKIP = -3;

    public Act(ReaderToTokenizerAdapter readerToTokenizerAdapter, final Object state, final Object event,
            final Performer[] performer) {
        super(state, event);
        this.readerToTokenizerAdapter = readerToTokenizerAdapter;
        this.performer = performer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.stateengine.Act#act()
     */
    public Object act() {
        final Object act = super.act();
        if (performer != null)
            for (int i = 0; i < performer.length; i++)
                performer[i].perform(getReaderToTokenizerAdapter());
        return act;
    }

    private ReaderToTokenizerAdapter getReaderToTokenizerAdapter() {
        return readerToTokenizerAdapter
        // readerTokenizerEngine .getReaderToTokenizerAdapter()
        ;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Act [state=" + state + ", event=" + event + ", result=" + result + ", performers=[" + getPerformers()
                + "]]";
    }

    private String getPerformers() {
        if (performer == null)
            return null;
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < performer.length; i++)
            sb.append("; ").append(performer[i]);
        return (sb.length() > 0 ? sb.substring(2) : "");
    }

}