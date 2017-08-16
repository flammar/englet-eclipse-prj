package am.englet;

import am.englet.Links.NextContentProvider;
import am.englet.Links.ValueConverter;

public class ValueConverterBasedNextContentProviderProxy implements
        NextContentProvider {

    private static final long serialVersionUID = 298791150870605862L;
    private final NextContentProvider base;
    private final ValueConverter valueConverter;

    public ValueConverterBasedNextContentProviderProxy(
            final NextContentProvider base, final ValueConverter valueConverter) {
        this.base = base;
        this.valueConverter = valueConverter;
    }

    public Object tryNextContent() {
        final Object tryNextContent = base.tryNextContent();
        return tryNextContent != null ? Links.nullCorrect(valueConverter
                .convert(Links.nullUncorrect(tryNextContent))) : null;
    }

}
