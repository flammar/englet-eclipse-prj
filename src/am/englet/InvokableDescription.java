/**
 * 08.12.2009
 *
 * 1
 *
 */
package am.englet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import am.englet.link.Link;

/**
 * @author 1
 * 
 */
public class InvokableDescription {
    private final Object sources[];
    private final Invokable invokable;
    private Integer multiKey = null;

    /**
     * @param inv
     * @param srcs
     */
    public InvokableDescription(final Invokable inv, final Object[] srcs) {
        invokable = inv;
        sources = Utils.copy(srcs);
    }

    static class PreparationContext {
        static class Counter {
            int timesUsed = 0;
            // int key = 1;
        }

        final Map found = new HashMap();
        int next = 1;

        void add(final InvokableDescription desc) {
            Counter counter = (Counter) found.get(desc);
            if (counter == null) {
                counter = new Counter();
                found.put(desc, counter);
            }
            counter.timesUsed++;
        }

        void setMultiKeys() {
            for (final Iterator i = found.entrySet().iterator(); i.hasNext();) {
                final Entry etr = (Entry) i.next();
                ((InvokableDescription) etr.getKey()).multiKey = ((Counter) etr
                        .getValue()).timesUsed == 1 ? null
                        : new Integer(next++);
            }
        }
    }

    public void prepare() {
        final PreparationContext context = new PreparationContext();
        prepare(this, context);
        context.setMultiKeys();
        Utils.debug(null, this, "");
    }

    private static void prepare(
            final InvokableDescription invokableDescription,
            final PreparationContext context) {
        context.add(invokableDescription);
        for (int i = 0; i < invokableDescription.sources.length; i++) {
            final Object source = invokableDescription.sources[i];
            if (source instanceof InvokableDescription)
                prepare((InvokableDescription) source, context);
            else
                invokableDescription.sources[i] = (source != null) ? new MethodsStorage.Direct(
                // {} kfen null-om Argument jaj ush em
                        ((source instanceof Link) && (((Link) source).next() == null)) ? ((Link) source)
                                .content()
                                : source)
                        : null;
        }
    }

    /**
     * @return the sources
     */
    public Object[] getSources() {
        return Utils.copy(sources);
    }

    /**
     * @return the invokable
     */
    public Invokable getInvokable() {
        return invokable;
    }

    /**
     * @return the multiKey
     */
    public Integer getMultiKey() {
        return multiKey;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        final StringBuffer res = new StringBuffer().append(super.toString())
                .append("[").append(multiKey).append("]: {");
        if (visited) {
            res.append("-visited-}");
            return res.toString();
        }
        visited = true;
        res.append(invokable).append(" sources=[");
        for (int i = 0; i < sources.length; i++)
            res.append("[").append(i).append("]=").append(
                    (sources[i] == this ? "this" : sources[i])).append(",");
        res.delete(res.length() - 1, res.length()).append("]}");
        visited = false;
        return res.toString();
    }

    private boolean visited = false;
}
