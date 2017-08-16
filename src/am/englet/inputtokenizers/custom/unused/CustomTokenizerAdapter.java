package am.englet.inputtokenizers.custom.unused;

import java.io.Reader;

import am.englet.inputtokenizers.ReaderToTokenizerAdapter;
import am.englet.inputtokenizers.ReaderTokenizerEngine;

public class CustomTokenizerAdapter extends ReaderToTokenizerAdapter {
    final private CharToEventConverter charToEventConverter;

    public CustomTokenizerAdapter(final Reader back, final CharToEventConverter charToEventConverter,
            final ReaderTokenizerEngine readerTokenizerEngine) {
        super(back);
        this.charToEventConverter = charToEventConverter;
        this.readerTokenizerEngine = readerTokenizerEngine;
        // readerTokenizerEngine.setReaderToTokenizerAdapter(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.inputtokenizers.ReaderToTokenizerAdapter#event(int)
     */
    protected String event(final int read) {
        final String event = super.event(read);
        return event != null ? event : charToEventConverter.event((char) read);
    }

}
