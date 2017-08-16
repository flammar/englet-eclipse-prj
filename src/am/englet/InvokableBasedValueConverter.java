package am.englet;

import am.englet.Links.ValueConverter;

public class InvokableBasedValueConverter implements ValueConverter {
    private static final long serialVersionUID = 6493429569105716000L;

    final Invokable invokable;
    final boolean isStatic;

    public InvokableBasedValueConverter(final Invokable invokable) {
        this.invokable = invokable;
        isStatic = invokable.targetType() == null;
    }

    public Object convert(final Object object) {
        try {
            return isStatic ? invokable.invoke(null, new Object[] { object })
                    : invokable.invoke(object, new Object[0]);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
