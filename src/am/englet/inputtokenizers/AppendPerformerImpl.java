package am.englet.inputtokenizers;

import am.englet.Utils;

public final class AppendPerformerImpl implements Performer {
    public void perform(final ReaderToTokenizerAdapter readerToTokenizerAdapter) {
        Utils.debug(null, "APPEND_PERFORMER", "");
        readerToTokenizerAdapter.append();
    }

    public String toString() {
        return "APPEND_PERFORMER@" + super.hashCode();
    }
}