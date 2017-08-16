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
    private String import_;
    private Link link;
    private final DataStack ds = new DataStack();

	public Object convert(final Candidate candidate) {
		try {
			final LookupContext initialLookupContext = candidate
					.getInitialLookupContext();
			if (import_ != null) {
				initialLookupContext.classPool.importPackages(import_.replaceAll("[^A-Za-z0-9\\._\\$]+", " ")
						.trim().split(" "));
				import_ = null;
			}
			final Englet englet = Utils.deriveEnglet(ds, initialLookupContext.methodsStorage,
					initialLookupContext.classPool);
			final Object run = am.englet.Utils.run(englet, candidate,
					link != null ? link : (link = getLink(englet)));
			return run;
		} catch (final Throwable e) {
			Utils.debug(null, e, command, "(", link, ") failed");
			e.printStackTrace();
		}
		return null;
    }

    private Link getLink(final Englet englet) throws Exception {
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
        for (final Iterator iterator = Collections.list(properties.propertyNames()).iterator(); iterator.hasNext();) {
            final String pname = (String) iterator.next();
            final StackFrame top = ds.top();
            top.put(pname, properties.get(pname));
        }
		import_ = (properties.getProperty("import", "") + " "
				+ properties.getProperty("import_packages", "") + " "
				+ properties.getProperty("importPackages", "")
				+ properties.getProperty("import_package", "") + " "
				+ properties.getProperty("importPackage", "")
				+ properties.getProperty("import.packages", "") + " " + properties
				.getProperty("import.package", ""));
	}

	public String toString() {
		return super.toString() + " [" + (link != null ? "link=" + link : "command=" + command) + "]";
	}
}
