package am.englet.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import am.englet.DataStack;
import am.englet.DataStack.StackFrame;
import am.englet.Englet;
import am.englet.Utils;
import am.englet.link.Link;

public class ProgrammableCandidateConverter implements CandidateConverter, Customizable {
    private String command;
    private Link link;
    private final DataStack ds = new DataStack();
    private ArrayList list;

    public Object convert(final Candidate candidate) {
        try {
            final LookupContext initialLookupContext = candidate.getInitialLookupContext();
            final Englet englet = Utils.deriveEnglet(ds, initialLookupContext.methodsStorage,
                    initialLookupContext.classPool);
            final Object run = am.englet.Utils.run(englet, candidate, link != null ? link : (link = getLink(list,
                    englet)));
            return run;
        } catch (final Throwable e) {
            Utils.debug(null, e, command, "(", link, ") failed");
            e.printStackTrace();
        }
        return null;
    }

    private Link getLink(final ArrayList list, final Englet englet) throws Exception {
        Utils.debug(null, "ProgrammableCandidateConverter.getLink():to parse:", command);
        englet.parse(command);
        Utils.debug(null, "ProgrammableCandidateConverter.getLink():parsed:", command);
        final DataStack ds2 = englet.getStack();
        Utils.debug(null, "ProgrammableCandidateConverter.getLink():to alink:", ds2);
        final Link link = am.englet.Management.alink(ds2, ds2.top().st.size());
        Utils.debug(null, "ProgrammableCandidateConverter.getLink():link:", link);
        return link;
    }

    public Object describeFail(final LookupContext lookupContext) {
        return toString();
    }

    public void customize(final Properties properties) {
        command = properties.getProperty("command");
        list = Collections.list(properties.propertyNames());
        for (final Iterator iterator = list.iterator(); iterator.hasNext();) {
            final String pname = (String) iterator.next();
            final StackFrame top = ds.top();
            top.put(pname, properties.get(pname));
        }
    }

    public String toString() {
        return super.toString() + " [" + (link != null ? "link=" + link : "command=" + command) + "]";
    }
}
