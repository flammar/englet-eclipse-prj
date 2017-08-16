package am.englet.lookup;

import am.englet.ArgumentProvider;
import am.englet.MethodsStorage;
import am.englet.cast.ClassPool;

public class LookupContext implements Candidate {
    public final MethodsStorage methodsStorage;
    public final String command;
    public final ArgumentProvider prov;
    public final ClassPool classPool;

    public LookupContext(final MethodsStorage methodsStorage, final String command, final ArgumentProvider prov,
            final ClassPool classPool) {
        this.methodsStorage = methodsStorage;
        this.command = command;
        this.prov = prov;
        this.classPool = classPool;
    }

    public String getCommandWithPrefix(final String prefix) {
        return prefix + command;
    }

    public Class[] getNNextArgumentTypes(final int argCount) {
        return prov.getNNextArgumentTypes(argCount);
    }

    public LookupContext getInitialLookupContext() {
        return this;
    }
}