package am.englet;

import am.englet.Links.NextContentProvider;
import am.englet.compoundncp.CurrentNextContentProviderSource;
import am.englet.compoundncp.CurrentProviderExhaustingHandler;
import am.englet.compoundncp.SpecialHandler;
import am.englet.compoundncp.SpecialityChecker;
import am.englet.link.Link;

public class StackedSENCPBase implements CurrentNextContentProviderSource,
        SpecialHandler, SpecialityChecker, CurrentProviderExhaustingHandler {
    public class Level {
        private final Level under;
        private final NextContentProvider nextContentProvider;
        private final int level;

        public Level(final Level under,
                final NextContentProvider nextContentProvider) {
            this.under = under;
            this.nextContentProvider = nextContentProvider;
            level = under != null ? under.level + 1 : 0;
        }
    }

    private Level top = null;
    private final int maxlevel;

    public StackedSENCPBase(final NextContentProvider base, final int maxlevel) {
        pushBufferProvider(base);
        this.maxlevel = maxlevel;
    }

    public StackedSENCPBase(final NextContentProvider base) {
        this(base, -1);
    }

    public boolean isSpecial(final Object content) {
        return content == Links.NULL || content instanceof Link;
    }

    public void handleSpecial(final Object content) {
        pushBufferProvider(new LinkBasedNextContentProvider((Link) Links
                .nullUncorrect(content)));
    }

    public NextContentProvider getCurrentProvider() {
        return top != null ? top.nextContentProvider : null;
    }

    public void handleCurrentProviderExhausting() {
        if (top != null)
            top = top.under;
    }

    private void pushBufferProvider(
            final NextContentProvider nextContentProvider) {
        top = new Level(top, nextContentProvider);

    }

    public boolean canHandleSpecial() {
        return maxlevel < 0 || top == null || top.level < maxlevel;
    }

}
