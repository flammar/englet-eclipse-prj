/**
 * 16.09.2012
 * 
 * 1
 * 
 */
package am.englet;

import am.englet.Links.ValueConverter;
import am.englet.link.Link;

public final class EngletBasedValueConverter implements
        ValueConverter {
    /**
     *
     */
    private static final long serialVersionUID = 2699490079032877523L;
    private final Englet englet;
    private final Link link;

    public EngletBasedValueConverter(final Englet englet, final Link link) {
        this.englet = englet;
        this.link = Utils.lazy(link);
    }

    public Object convert(final Object o) {
        try {
            final Object run = Utils.run(englet, o, link);
            return Utils.STACK_IS_EMPTY == run ? Boolean.FALSE : run;
        } catch (final Throwable e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }
}