package am.englet.lookup;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import am.englet.Invokable;
import am.englet.Utils;

public class ExtraLookup {
    private static final SimpleInvokableCandidateSourceFactory SIMPLE_INVOKABLE_CANDIDATE_SOURCE_FACTORY = new SimpleInvokableCandidateSourceFactory();

    public static boolean lookUp(final String key, final LookupContext context) {
        final Invokable realisation = ExtraLookup.getRealisation(key, context);
        return Lookup.tryToAdapt(context, realisation, key);
    }

    public static Invokable getRealisation(final String key, final LookupContext context) {
        final Properties props = ExtraLookup.getLookupProperties(key);
        final Iterator propsIt = ExtraLookup.createPropertiesIterator(props);
        final Iterator iterator = new Iterator() {

            public void remove() {
            }

            public Object next() {
                final Properties pr = (Properties) propsIt.next();
                return ExtraLookup.SIMPLE_INVOKABLE_CANDIDATE_SOURCE_FACTORY.getInstance(pr);
            }

            public boolean hasNext() {
                return propsIt.hasNext();
            }
        };
        final InvokableCandidate instance = new IteratorBasedInvokableCandidateSource(iterator, context)
                .getInstance(context);
        final Invokable realisation = instance.getRealisation();
        return realisation;
    }

    private static Iterator createPropertiesIterator(final Properties props) {
        final Enumeration propertyNames = props.propertyNames();
        final TreeSet treeSet = new TreeSet();
        while (propertyNames.hasMoreElements())
            treeSet.add(propertyNames.nextElement());
        final SortedSet keys = Collections.unmodifiableSortedSet(treeSet);
        return new Iterator() {

            private boolean wasHasNext = false;
            private int counter = -1;
            private boolean failed = false;

            public void remove() {
            }

            public Object next() {
                if (!wasHasNext && !hasNext())
                    return null;
                final Properties properties = new Properties(props);
                final String fromElement = prefix();
                final int length = fromElement.length();
                final SortedSet subSet = keys.subSet(fromElement, (counter + 1) + ".");
                for (final Iterator iterator = subSet.iterator(); iterator.hasNext();) {
                    final String key = (String) iterator.next();
                    properties.setProperty(key.substring(length), props.getProperty(key));
                }
                return properties;
            }

            private String prefix() {
                return counter + ".";
            }

            public boolean hasNext() {
                return !failed && (!(failed = !hasNext0()));
            }

            private boolean hasNext0() {
                counter++;
                final String fromElement = prefix();
                final SortedSet tailSet = keys.tailSet(fromElement);
                wasHasNext = true;
                return tailSet != null && !tailSet.isEmpty() && tailSet.first().toString().startsWith(fromElement);
            }
        };

    }

    private static Properties getLookupProperties(final String key) {
        final Properties properties = Utils.getPropertiesByResource(Lookup.class, "lookup.properties");
        final Properties properties2 = new Properties(properties);
        properties2.putAll(properties);
        return properties2;
    }
}
