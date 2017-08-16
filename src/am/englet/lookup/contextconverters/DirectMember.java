package am.englet.lookup.contextconverters;

import java.util.Arrays;
import java.util.List;

import am.englet.ConstructorInvokable;
import am.englet.FieldGetInvokable;
import am.englet.FieldSetInvokable;
import am.englet.Links.ValueConverter;
import am.englet.MethodInvokable;
import am.englet.Utils;
import am.englet.cast.ClassPool;
import am.englet.lookup.Candidate;
import am.englet.lookup.CandidateConverter;
import am.englet.lookup.LookupContext;
import am.englet.lookup.LookupUtils;

public class DirectMember implements CandidateConverter {

    public Object convert(final Candidate candidate) {
        final LookupContext initialLookupContext = candidate.getInitialLookupContext();
        final String command = initialLookupContext.command;
        final int length = command.length();
        if (command.charAt(0) != '(' || command.charAt(length - 1) != ')')
            return null;
        final String substring = command.substring(1, length - 1);
        final String[] split = substring.split("/");
        final List asList = Arrays.asList(split);
        final int lastIndex = asList.size() - 1;
        if (lastIndex < 1)
            return null;
        final List subList = asList.subList(1, lastIndex);
        final ClassPool classPool = initialLookupContext.classPool;
        final Class forName = classPool.forName(asList.get(0).toString());
        if (forName == null)
            return null;
        final String name0 = asList.get(lastIndex).toString();
        final boolean isSetter = name0.endsWith("!");
        final String name = isSetter ? name0.substring(0, name0.length() - 1) : name0;

        if (isSetter)
            try {
                return new FieldSetInvokable(forName.getField(name));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        final Class[] array = (Class[]) LookupUtils.convert(subList, new ValueConverter() {
            private static final long serialVersionUID = 1864413243073366440L;

            public Object convert(final Object object) {
                return classPool.forName(object.toString());
            }
        }).toArray(new Class[0]);
        if (Arrays.asList(array).contains(null))
            return null;
        if ("new".equals(name))
            try {
                return new ConstructorInvokable(forName.getConstructor(array));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        else if (array.length == 0)
            try {
                return new FieldGetInvokable(forName.getField(name));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        try {
            return new MethodInvokable(forName.getMethod(name, array));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object describeFail(final LookupContext lookupContext) {
        return Utils.simpleName(getClass()) + ": Looking for " + lookupContext.command + " failed";
    }

}
