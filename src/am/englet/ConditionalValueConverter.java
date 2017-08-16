package am.englet;

import java.lang.reflect.InvocationTargetException;

import am.englet.Links.ValueConverter;

// TODO jav nying on T nA om em
/**
 * 2012-09-22 zex nying on
 * 
 * @author Acer
 * 
 */
public class ConditionalValueConverter implements ValueConverter {

    private static final long serialVersionUID = -5037674603721880045L;
    final private Invokable condition, converter;
    final private boolean condStatic;
    final private boolean convStatic;

    public ConditionalValueConverter(final Invokable condition,
            final Invokable converter) {
        this.condition = condition;
        this.converter = converter;
        condStatic = condition.targetType() == null;
        convStatic = converter.targetType() == null;
    }

    public Object convert(final Object object) {
        try {
            return Utils.toBoolean(invoke(object, condition, condStatic)) ? invoke(
                    object, converter, convStatic)
                    : object;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object invoke(final Object object, final Invokable converter2,
            final boolean isStatic) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            InstantiationException {
        return isStatic ? converter2.invoke(null, new Object[] { object })
                : converter2.invoke(object, new Object[0]);
    }

}
