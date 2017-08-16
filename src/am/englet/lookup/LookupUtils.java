package am.englet.lookup;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import am.englet.CodeBlock;
import am.englet.ConstructorInvokable;
import am.englet.Invokable;
import am.englet.Links;
import am.englet.Links.ValueConverter;
import am.englet.MethodInvokable;
import am.englet.Utils;
import am.englet.reflect.MemberInvokable;
import am.englet.util.Checker;
import am.englet.util.ProviderBasedUnmodifiableList;
import am.englet.util.UnmodifiableListDataProvider;

public class LookupUtils {

    private static final Iterator EMPTY_ITERATOR = Collections.EMPTY_SET.iterator();

    public static InvokableCandidate createBackedCandidate(final InvokableCandidate a, final InvokableCandidate b) {
        return new IteratorBasedInvokableCandidate(Arrays.asList(new InvokableCandidate[] { a, b }).iterator());
    };

    public static InvokableCandidate wrapToFailReporter(final InvokableCandidate candidate, final CodeBlock failReporter) {
        return new InvokableCandidate() {

            public Invokable getRealisation() {
                final Invokable realisation = candidate.getRealisation();
                if (realisation == null)
                    Utils.debug(System.out, LookupUtils.stringify(failReporter.result()), " failed");
                return realisation;
            }
        };
    }

    public static Collection convert(final Collection base, final ValueConverter converter) {
        return new AbstractCollection() {

            public int size() {
                return base.size();
            }

            public Iterator iterator() {
                return createConvertingIterator(base.iterator(), converter);
            }
        };
    }

    public static List convert(final List base, final ValueConverter converter) {
        return new AbstractList() {

            public int size() {
                return base.size();
            }

            public Object get(final int index) {
                return converter.convert(base.get(index));
            }
        };
    }

    public static InvokableCandidate wrapToFailReporter(final InvokableCandidate candidate, final String failReport) {
        return new InvokableCandidate() {

            public Invokable getRealisation() {
                final Invokable realisation = candidate.getRealisation();
                if (realisation == null)
                    Utils.debug(System.out, failReport, " failed");
                return realisation;
            }
        };
    }

    public static InvokableCandidate wrapToFailReporter(final InvokableCandidate candidate) {
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        final Object failReport = stackTrace[1].toString();
        return new InvokableCandidate() {

            public Invokable getRealisation() {
                final Invokable realisation = candidate.getRealisation();
                if (realisation == null)
                    Utils.debug(System.out, failReport, " failed");
                return realisation;
            }
        };
    }

    public static Iterator createConvertingIterator(final Iterator base, final Links.ValueConverter converter) {
        return new Iterator() {

            public void remove() {
                base.remove();
            }

            public Object next() {
                return converter.convert(base.next());
            }

            public boolean hasNext() {
                return base.hasNext();
            }
        };
    }

    public static Iterator createFlattenedIterator(final Iterator base) {
        return new Iterator() {
            private Iterator current;

            public boolean hasNext() {
                return getCurrent().hasNext();
            }

            public Object next() {
                return getCurrent().next();
            }

            public void remove() {
                getCurrent().remove();
            }

            private Iterator getCurrent() {
                if (current != null)
                    return current;
                else if (base.hasNext()) {
                    final Object next = base.next();
                    return (current = next instanceof Iterator ? (Iterator) next
                            : next instanceof Collection ? ((Collection) next).iterator() : Collections.singleton(next)
                                    .iterator());
                } else
                    return EMPTY_ITERATOR;
            }
        };
    }

    public static Collection createLazyJiontCollection(final Collection[] colls) {
        return new AbstractCollection() {
            private boolean dirty = true;
            private int size;
            private final Collection baseList = Arrays.asList(colls);

            public int size() {
                if (dirty)
                    recalculatesize();
                return size;
            }

            private void recalculatesize() {
                int s = 0;
                for (int i = 0; i < colls.length; i++)
                    s += colls[i].size();
                size = s;
                dirty = false;
            }

            public Iterator iterator() {
                final ValueConverter converter = new ValueConverter() {

                    private static final long serialVersionUID = 1L;

                    public Object convert(final Object object) {
                        return ((Collection) object).iterator();
                    }
                };
                final Iterator base = createConvertingIterator(baseList.iterator(), converter);
                return createFlattenedIterator(base);
            }
        };
    }

    public static String stringify(final Object obj) {
        if (obj instanceof Collection) {
            final Iterator iterator = ((Collection) obj).iterator();
            final StringBuffer b = new StringBuffer();
            while (iterator.hasNext()) {
                final Object object = iterator.next();
                b.append(object);
            }
            return b.toString();
        } else
            return obj != null ? obj.toString() : "";
    }

    private static String rxquote(final String s) {
        int slashEIndex = s.indexOf("\\E");
        if (slashEIndex == -1)
            return "\\Q" + s + "\\E";

        final StringBuffer sb = new StringBuffer(s.length() * 2);
        sb.append("\\Q");
        slashEIndex = 0;
        int current = 0;
        while ((slashEIndex = s.indexOf("\\E", current)) != -1) {
            sb.append(s.substring(current, slashEIndex));
            current = slashEIndex + 2;
            sb.append("\\E\\\\E\\Q");
        }
        sb.append(s.substring(current, s.length()));
        sb.append("\\E");
        return sb.toString();
    }

    public static CandidateConverter checking(final CandidateConverter converter, final Checker checker) {
        return new CandidateConverter() {

            public Object convert(final Candidate candidate) {
                final boolean check = checker.check(candidate);
                return check ? converter.convert(candidate) : null;
            }

            public Object describeFail(final LookupContext lookupContext) {
                final Object describeFail = converter.describeFail(lookupContext);
                final Object describeFail1 = describeFail instanceof CodeBlock ? ((CodeBlock) describeFail).result()
                        : describeFail;
                return "Condition failed for: " + describeFail1;
            }
        };
    }

    public static CandidateConverter or(final CandidateConverter[] converters) {
        return new CandidateConverter() {

            public Object describeFail(final LookupContext lookupContext) {
                return new CodeBlock() {

                    public Object result() {
                        final List convert = LookupUtils.convert(Arrays.asList(converters), new ValueConverter() {
                            private static final long serialVersionUID = -616158085777315355L;

                            public Object convert(final Object object) {
                                return ((CandidateConverter) object).describeFail(lookupContext);
                            }
                        });
                        final List joinList = joinList(convert, " failed, ");
                        return stringify(joinList) + " failed";
                    }
                };
            }

            public Object convert(final Candidate candidate) {
                for (int i = 0; i < converters.length; i++) {
                    final Object convert = converters[i].convert(candidate);
                    if (convert != null)
                        return convert;
                }
                return null;
            }
        };
    }

    public static List splitList(final String string, final String opening, final String closing) {
        final List splitList = LookupUtils.splitList(string, closing);
        final List listList = new AbstractList() {
            final List back = Arrays.asList(new Object[splitList.size()]);

            public int size() {
                return splitList.size();
            }

            public Object get(final int index) {
                final Object object = back.get(index);
                if (object != null)
                    return object;
                final String string2 = splitList.get(index).toString();
                final List splitList2 = LookupUtils.splitList(string2, opening);
                // final List joinList = LookupUtils.joinList(splitList2,
                // getMatch(string2, startRegex));
                final List joinList = LookupUtils.joinList(splitList2, opening);
                back.set(index, joinList);
                return get(index);
            }
        };
        // final List list = LookupUtils.joinList(listList,
        // Collections.singletonList(getMatch(string, endRegex)));
        final List list = LookupUtils.joinList(listList, Collections.singletonList(closing));
        final Collection coll1 = LookupUtils.listListAsCollection(list);
        final ArrayList arrayList = new ArrayList(coll1);
        return Collections.unmodifiableList(arrayList);
    }

    // private static String getMatch(final String string, final String regex) {
    // final Matcher matcher = Pattern.compile(regex).matcher(string);
    // final String str = matcher.find() ? matcher.group() : "";
    // return str;
    // }

    // public static List splitList(final String string, final String regex) {
    // System.out.println("[" + string + "],[" + regex + "]");
    // final boolean endsWith = string.endsWith(regex);
    // System.out.println(endsWith);
    // final List asList = Arrays.asList((endsWith ?
    // LookupUtils.randTail(string) : string).split(regex, -1));
    // if (endsWith)
    // asList.set(asList.size() - 1, "");
    // return Collections.unmodifiableList(asList);
    // }
    public static List splitList(final String string, final String regex) {
        return Collections.unmodifiableList(Arrays.asList(string.split(rxquote(regex), -1)));
    }

    public static List joinList(final List base, final Object comma) {
        return base == null || base.isEmpty() ? base : new AbstractList() {
            final int size = (base.size() - 1) * 2 + 1;

            public int size() {
                return size;
            }

            public Object get(final int index) {
                return index % 2 == 0 ? base.get(index / 2) : comma;
            }
        };
    }

    public static Collection listListAsCollection(final List list) {
        return new AbstractCollection() {
            int size = -1;

            public int size() {
                if (size >= 0)
                    return size;
                int newSize = 0;
                for (final Iterator iterator = list.iterator(); iterator.hasNext(); newSize += ((List) iterator.next())
                        .size())
                    ;
                size = newSize;
                return size();
            }

            public Iterator iterator() {
                final Iterator iterator = list.iterator();
                return new Iterator() {
                    private Iterator current;

                    public void remove() {
                        throw new UnsupportedOperationException("remove not supported");
                    }

                    public Object next() {
                        return hasNext() ? current.next() : null;
                    }

                    public boolean hasNext() {
                        if (current == null) {
                            if (!iterator.hasNext())
                                return false;
                            current = ((List) iterator.next()).iterator();
                        }
                        if (!current.hasNext()) {
                            current = null;
                            return hasNext();
                        }
                        return true;
                    }
                };
            }
        };
    }

    private static String randTail(final String property) {
        String randomString;
        do
            randomString = LookupUtils.randomString();
        while (property.indexOf(randomString) >= 0);
        return property + randomString;
    }

    public static String randomString() {
        return Integer.toHexString(LookupUtils.rnd.nextInt());
    }

    static final Random rnd = new Random();

    static InvokableCandidate getInstantiatorCandidate(final Class cls, final Class[] types) {
        return new InvokableCandidate() {
            public Invokable getRealisation() {
                return LookupUtils.getCheckedInstantiator(LookupUtils.getInstantiator(cls, types));
            }
        };
    }

    static CodeBlock createInstantiatorCandidateFailDescription(final Class cls, final Class[] types) {
        return new CodeBlock() {
            public Object result() {
                final ProviderBasedUnmodifiableList providerBasedUnmodifiableList = new ProviderBasedUnmodifiableList(
                        new UnmodifiableListDataProvider() {
                            final String s = "Lookup.tryToAdaptInstantiator():fail:cls:";

                            public int size() {
                                return types.length + 1;
                            }

                            public Object get(final int i) {
                                return (i == 0 ? s + "" + cls : "argClasses[" + (i - 1) + "]:" + types[i - 1]) + "\r\n";
                            }
                        });
                return providerBasedUnmodifiableList;
            }
        };
    }

    static List array2list(final Object array) {
        return Collections.unmodifiableList(new AbstractList() {

            public int size() {
                return Array.getLength(array);
            }

            public Object get(final int paramInt) {
                return Array.get(array, paramInt);
            }
        });
    }

    private static MemberInvokable getCheckedInstantiator(final MemberInvokable instantiator) {
        return ((instantiator != null) && (!(instantiator instanceof ConstructorInvokable && Modifier
                .isAbstract(instantiator.returnType().getModifiers()))))
                || (instantiator instanceof MethodInvokable) ? instantiator : null;
    }

    private static MemberInvokable getInstantiator(final Class cls, final Class[] types) {
        final MemberInvokable res = cls == null ? null : Utils.instantiator(cls, types);
        Utils.debug(System.out, new CodeBlock() {

            public Object result() {
                return Arrays.asList(new Object[] { "getInstantiator: ", res, " <= cls: ", cls, ", types: ",
                        Arrays.asList(types) });
            }
        });
        return res;
    }
}
