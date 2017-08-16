package am.englet.inputtokenizers;

import am.englet.Utils;

public final class SkipPerformerImpl implements Performer {
    public String toString() {
        Utils.debug(null, "SKIP_PERFORMER", "");
        return "SKIP_PERFORMER@" + super.hashCode();
    }

    public void perform(final ReaderToTokenizerAdapter readerToTokenizerAdapter) {

    }
}