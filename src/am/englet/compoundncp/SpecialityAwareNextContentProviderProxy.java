package am.englet.compoundncp;

import am.englet.Links.NextContentProvider;

public class SpecialityAwareNextContentProviderProxy implements
        NextContentProvider {

    private static final long serialVersionUID = -4372446964112544653L;
    private final SpecialHandler specialHandler;
    private final SpecialityChecker specialityChecker;
    private final NextContentProvider nextContentProvider;

    public SpecialityAwareNextContentProviderProxy(
            final NextContentProvider nextContentProvider,
            final SpecialityChecker specialityChecker,
            final SpecialHandler specialHandler) {
        this.nextContentProvider = nextContentProvider;
        this.specialityChecker = specialityChecker;
        this.specialHandler = specialHandler;
    }

    public Object tryNextContent() {
        while (true) {
            final Object tryNextContent = nextContentProvider.tryNextContent();
            if (specialityChecker.isSpecial(tryNextContent)
                    && specialHandler.canHandleSpecial()) {
                specialHandler.handleSpecial(tryNextContent);
                continue;
            }
            return tryNextContent;
        }
    }
}
