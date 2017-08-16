package am.englet.compoundncp;

import am.englet.Links.NextContentProvider;

public class SourcedExhaustableNextContentProvider implements
        NextContentProvider {

    private static final long serialVersionUID = -331115579848635538L;
    private final CurrentNextContentProviderSource contentProviderSource;
    private final CurrentProviderExhaustingHandler currentProviderExhaustingHandler;

    public SourcedExhaustableNextContentProvider(
            final CurrentNextContentProviderSource nextContentProviderProvider,
            final CurrentProviderExhaustingHandler currentProviderExhaustingHandler) {
        this.contentProviderSource = nextContentProviderProvider;
        this.currentProviderExhaustingHandler = currentProviderExhaustingHandler;
    }

    public Object tryNextContent() {
        while (true) {
            final NextContentProvider currentProvider = contentProviderSource
                    .getCurrentProvider();
            if (currentProvider == null)
                return null;
            final Object tryNextContent = currentProvider.tryNextContent();
            if (tryNextContent == null)
                currentProviderExhaustingHandler
                        .handleCurrentProviderExhausting();
            else
                return tryNextContent;
        }

    }

}
