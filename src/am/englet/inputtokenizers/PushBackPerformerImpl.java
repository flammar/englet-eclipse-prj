package am.englet.inputtokenizers;

import am.englet.Utils;

public final class PushBackPerformerImpl implements Performer {
    public void perform(final ReaderToTokenizerAdapter readerToTokenizerAdapter) {
        Utils.debug(null, "PUSHBACK_PERFORMER", "");
        readerToTokenizerAdapter.unread();
    }

    public String toString() {
        return "PUSHBACK_PERFORMER@" + super.hashCode();
    }
}