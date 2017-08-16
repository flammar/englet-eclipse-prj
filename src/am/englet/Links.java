package am.englet;

import java.io.Serializable;
import java.util.Stack;

import am.englet.link.FinalLink;
import am.englet.link.Link;
import am.englet.util.Checker;

public class Links {

    // de paixsraibhjhem on: k'sz ment NextContentProvider.tryNextContent()'sz
    // uj TryNextContentBased.calculateNext()'m jav pasz Gebh javal k'avemI
    public transient final static Object NULL = new Object();

    static Link plusz(final Link link, final Link link2) {
        return nextContentProviderBased(new NextContentProvider() {
            private static final long serialVersionUID = -1441685040474609337L;
            Link l1 = link;
            Link l2 = link2;

            public Object tryNextContent() {
                if ((l1 == null) || (l2 == null))
                    return null;
                try {
                    return new FinalLink(l1.content(), new FinalLink(l2
                            .content()));
                } finally {
                    l1 = l1.next();
                    l2 = l2.next();
                }
            }
        });
    }

    static Link div(final Link l, final int div) {
        final Object[] buv = new Object[div];
        return nextContentProviderBased(new NextContentProvider() {
            /**
             *
             */
            private static final long serialVersionUID = 6260494251896155217L;
            Link link = l;

            public Object tryNextContent() {
                {
                    final int length = buv.length;
                    int i = 0;
                    for (; (i < length) && (link != null); link = link.next())
                        buv[i++] = link.content();
                    if (i < length)
                        return null;
                }
                return toChain(buv);
            }
        });
    }

    public static FinalLink toChain(final Object[] buv) {
        final int length = buv.length;
        FinalLink l2 = null;
        for (int i = length; i-- > 0; l2 = new FinalLink(buv[i], l2))
            ;
        return l2;
    }

    public static Link nextContentProviderBased(final NextContentProvider ncp) {
        return new TryNextContentBased(null, ncp).next();
    }

    public static Link checkerBased(final Link link, final Checker checker) {
        return nextContentProviderBased(new NextContentProvider() {
            private static final long serialVersionUID = 1L;
            private Link linkA = link;

            public Object tryNextContent() {
                while (linkA != null) {
                    final Object content = linkA.content();
                    linkA = linkA.next();
                    final boolean check = checker.check(content);
                    if (check)
                        return nullCorrect(content);
                }
                return null;
            }
        });
    }

    // TODO rewrite
    public static Link valueConverterBased(final Link link,
            final ValueConverter vc) {
        return nextContentProviderBased(new LinkConvertingNextContentProvider(
                link, vc));
    }

    public static Link recursiveAtomValueConverterBased(final Link link,
            final ValueConverter vc) {
        return nextContentProviderBased(new LinkConvertingNextContentProvider(
                link, vc) {
            private static final long serialVersionUID = -747006159837527872L;

            protected Object convert(final Object a) {
                final Object a1 = a instanceof Link ? converAsLink(a)
                        : basicConvert(a);
                return a1 instanceof Link ? converAsLink(a1) : a1;
            }

            private Link converAsLink(final Object a1) {
                return recursiveAtomValueConverterBased((Link) a1,
                        valueConverter);
            }
        });
    }

    public static Link recursiveValueConverterBased(final Link link,
            final ValueConverter vc) {
        return nextContentProviderBased(new LinkConvertingNextContentProvider(
                link, vc) {

            private static final long serialVersionUID = 6959160227494359321L;

            protected Object convert(final Object a) {
                final Object convert = basicConvert(a);
                return convert instanceof Link ? recursiveValueConverterBased(
                        (Link) convert, valueConverter) : convert;
            }
        });
    }

    public static Link flat(final Link link) {
        return flat(link, -1);
    }

    public static Link flat(final Link link, final int limit) {
        final Stack st = new Stack();
        st.push(link);
        return nextContentProviderBased(new NextContentProvider() {
            private static final long serialVersionUID = 1L;

            public Object tryNextContent() {
                while (true) {
                    if (st.size() <= 0)
                        return null;
                    while (true) {
                        final int i = st.size() - 1;
                        if (st.elementAt(i) != null)
                            break;
                        if (i <= 0)
                            return null;
                        else
                            st.removeElementAt(i);
                    }
                    final int i = st.size() - 1;
                    final Link link2 = (Link) st.get(i);
                    final Object res = link2.content();
                    st.set(i, link2.next());
                    if ((limit >= 0) && (i >= limit))
                        return res;
                    else if (res instanceof Link)
                        st.push(res);
                    else if (res != null)
                        return res;
                }
            }
        });
    }

    public static Link toLazyChain(final String[] buv) {
        return nextContentProviderBased(new NextContentProvider() {
            private static final long serialVersionUID = 5663484846369597083L;
            int i = 0;
            private final int l = buv.length;

            public Object tryNextContent() {
                return i < l ? nullCorrect(buv[i++]) : null;
            }
        });
    }

    public static Object nullCorrect(final Object object) {
        return object != null ? object : NULL;
    }

    public static boolean isCorrectedLink(final Object object) {
        return object instanceof Link || object == NULL;
    }

    private static class LinkConvertingNextContentProvider implements
            NextContentProvider {
        private static final long serialVersionUID = 1724034357948390110L;
        private Link linkA;
        protected final ValueConverter valueConverter;

        private LinkConvertingNextContentProvider(final Link link,
                final ValueConverter vc) {
            linkA = link;
            valueConverter = vc;
        }

        public final Object tryNextContent() {
            try {
                Object a;
                return (linkA == null) ? null
                        : (a = linkA.content()) != null ? nullCorrect(convert(a))
                                : NULL;
            } finally {
                if (linkA != null)
                    linkA = linkA.next();
            }
        }

        protected Object convert(final Object a) {
            return basicConvert(a);
        }

        protected final Object basicConvert(final Object a) {
            return valueConverter.convert(a);
        }
    }

    public static interface NextContentProvider extends Serializable {
        Object tryNextContent();
    }

    public static interface ValueConverter extends Serializable {
        public static class ToSameValueConverter implements ValueConverter {
            private static final long serialVersionUID = -3943934115193894156L;

            public Object convert(final Object object) {
                return object;
            }
        }

        Object convert(Object object);

        public static final ToSameValueConverter TO_SAME_VALUE_CONVERTER = new ToSameValueConverter();
    }

    public static class TryNextContentBased extends Calculate_Next_Based {
        private static final long serialVersionUID = -1956213711987664808L;
        final private Object content;
        private NextContentProvider ncp;

        public TryNextContentBased(final Object content,
                final NextContentProvider ncp) {
            this.content = content;
            this.ncp = ncp;
        }

        public final Object content() {
            return content;
        }

        protected Calculate_Next_Based calculateNext() {
            final Object ñontent = ncp != null ? ncp.tryNextContent() : null;
            if (ñontent == null)
                ncp = null;
            return ñontent != null ? new TryNextContentBased(
                    ñontent == NULL ? null : ñontent, ncp) : null;
        }

    }

    public abstract static class Calculate_Next_Based implements
            Link.Serializable {
        private static final long serialVersionUID = 2153999840816608531L;
        private Calculate_Next_Based next;

        public final Link next() {
            return next != null ? next : (next = calculateNext());
        }

        protected abstract Calculate_Next_Based calculateNext();
    }

    public static Object nullUncorrect(final Object object) {
        return object == NULL ? null : object;
    }
}
