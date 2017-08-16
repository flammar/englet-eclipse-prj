/**
 * 02.07.2010
 *
 * 1
 *
 */
package am.englet.inputtokenizers;

import am.englet.stateengine.Engine;

public class ReaderTokenizerEngine extends Engine implements Cloneable {

    private static final long serialVersionUID = 3716485343617401264L;

    public Act PUSHBACK/* = newAct(PUSHBACK_PERFORMER) */;

    public Act APPEND/* = newAct(APPEND_PERFORMER) */;

    public Act SKIP/* = newAct(SKIP_PERFORMER) */;

    public ReaderTokenizerEngine() {
        // setReaderToTokenizerAdapter(readerToTokenizerAdapter);
        // init(readerToTokenizerAdapter);
    }

    // void init(final ReaderToTokenizerAdapter readerToTokenizerAdapter) {
    // PUSHBACK = newAct(readerToTokenizerAdapter, PUSHBACK_PERFORMER);
    // APPEND = newAct(readerToTokenizerAdapter, APPEND_PERFORMER);
    // SKIP = newAct(readerToTokenizerAdapter, SKIP_PERFORMER);
    // }

    // private ReaderTokenizerEngine() {
    // }

    // public void setReaderToTokenizerAdapter(final ReaderToTokenizerAdapter
    // engletParseReaderToTokenizerAdapter) {
    // readerToTokenizerAdapter = engletParseReaderToTokenizerAdapter;
    // if (engletParseReaderToTokenizerAdapter.getReaderTokenizerEngine() !=
    // this)
    // engletParseReaderToTokenizerAdapter.setReaderTokenizerEngine(this);
    // }
    //
    // public ReaderToTokenizerAdapter getReaderToTokenizerAdapter() {
    // return readerToTokenizerAdapter;
    // }

    public Act newAct(final ReaderToTokenizerAdapter readerToTokenizerAdapter3, final Performer performer) {
        return new Act(readerToTokenizerAdapter3, "", "", new Performer[] { performer });
    }

    public Act newAct(final ReaderToTokenizerAdapter readerToTokenizerAdapter2, final Performer[] performers) {
        // final ReaderToTokenizerAdapter readerToTokenizerAdapter2 =
        // readerToTokenizerAdapter;
        return new Act(readerToTokenizerAdapter2, "", "", performers);
    }

    public Act newAct(final ReaderToTokenizerAdapter readerToTokenizerAdapter2, final Object object) {
        // final ReaderToTokenizerAdapter readerToTokenizerAdapter2 =
        // readerToTokenizerAdapter;
        return new Act(readerToTokenizerAdapter2, "", "", new Performer[] { objectAppendPerformer(object) });
    }

    public/* static */Performer objectAppendPerformer(final String object) {
        return objectAppendPerformer((Object) object);
    }

    public/* static */Performer objectAppendPerformer(final Object object) {
        return new Performer() {

            public void perform(final ReaderToTokenizerAdapter readerToTokenizerAdapter) {
                readerToTokenizerAdapter.append(object);
            }

            public String toString() {
                return "ObjectAppendPerformer:" + object + " @" + super.hashCode();
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    protected Object clone() throws CloneNotSupportedException {
        final ReaderTokenizerEngine clone = (ReaderTokenizerEngine) super.clone();
        // clone.readerToTokenizerAdapter = null;
        clone.state = DEFAULT;
        return clone;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "ReaderTokenizerEngine [rules=" + rules + "]";
    }

}