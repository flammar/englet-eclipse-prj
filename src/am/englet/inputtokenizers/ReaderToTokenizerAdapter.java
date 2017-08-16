package am.englet.inputtokenizers;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import am.englet.Utils;
import am.englet.link.AdapterMetadata;
import am.englet.link.TrivialBaseBackAdapterImpl;
import am.englet.link.backadapters.LineReaderStrategy;

public class ReaderToTokenizerAdapter extends TrivialBaseBackAdapterImpl {

    private char current;
    private final StringBuffer buf = new StringBuffer();
    protected ReaderTokenizerEngine readerTokenizerEngine = prepareReaderTokenizerEngine();

    private static final AdapterMetadata metadata = new AdapterMetadata(Reader.class,
            new Class[] { LineReaderStrategy.class });
    protected static final String BASIC = "BASIC";
    protected static final String EOF = "EOF";
    protected static final String INITIAL = "INITIAL";

    public ReaderToTokenizerAdapter() {
        super();
    }

    public ReaderToTokenizerAdapter(final Object back) {
        super(new PushbackReader1((Reader) back));
    }

    public void append(final Object action2) {
        buf.append(action2);
    }

    public void append() {
        buf.append(current);
    }

    public void unread() {
        try {
            getBack().unread(current);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    protected PushbackReader getBack() {
        return (PushbackReader) back;
    }

    public Object metadata() {
        final PushbackReader back2 = getBack();
        return back2 == null ? (Object) metadata : back2;
    }

    private ReaderTokenizerEngine prepareReaderTokenizerEngine() {
        final ReaderTokenizerEngine readerTokenizerEngine2 = new ReaderTokenizerEngine();
        readerTokenizerEngine2.PUSHBACK = readerTokenizerEngine2.newAct(this, new PushBackPerformerImpl());
        readerTokenizerEngine2.APPEND = readerTokenizerEngine2.newAct(this, new AppendPerformerImpl());
        readerTokenizerEngine2.SKIP = readerTokenizerEngine2.newAct(this, new SkipPerformerImpl());
        return readerTokenizerEngine2;
    }

    private int read() {
        try {
            return getBack().read();
        } catch (final IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    protected String event(final int read) {
        return read == -1 ? EOF : null;
    }

    public Object getNext() {
        if (buf.length() > 0) {
            final String string = buf.toString();
            buf.setLength(0);
            Utils.debug(null, "ReaderToTokenizerAdapter.getNext():", string);
            return string;
        } else if (readerTokenizerEngine.finished())
            return null;
        else {
            tryToFillBuffer();
            return getNext();
        }
    }

    /**
     * Al kWum.
     */
    protected void tryToFillBuffer() {
        readerTokenizerEngine.init(INITIAL);
        while (!(readerTokenizerEngine.is(BASIC) || readerTokenizerEngine.finished())) {
            Utils.debug(System.out, readerTokenizerEngine.getState(), "");
            ;
            next1();
        }

    }

    protected void next1() {
        final int read = read();
        current = (char) read;
        try {
            readerTokenizerEngine.act(event(read));
        } catch (final IllegalArgumentException e) {
            // TODO: IllegalArgumentException frum najerT jan
            Utils.outPrintln(System.out, "ReaderTokenizerEngine:IllegalState:readerToTokenizerAdapter.buf:" + buf);
            throw e;
        }
        return;
    }

    /**
     * @param readerTokenizerEngine
     *            the readerTokenizerEngine to set
     */
    public void setReaderTokenizerEngine(final ReaderTokenizerEngine readerTokenizerEngine) {
        this.readerTokenizerEngine = readerTokenizerEngine;
    }

    /**
     * @return the readerTokenizerEngine
     */
    public ReaderTokenizerEngine getReaderTokenizerEngine() {
        return readerTokenizerEngine;
    }

}