package am.englet.macro;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.englet.CommandSource;
import am.englet.Englet;
import am.englet.Invokable;
import am.englet.ResultHandler;
import am.englet.ServiceObject;
import am.englet.link.Link;

public class MacroContainerInvokable implements Invokable {
    //@formatter:off
    // egl 'am.englet.macro import_package ['Integer .class] 'Class .class 0 array_0_new_instance to_array {2+} macro_container_invokable 7 @y!
//@formatter:on

    private static final Class[] A = new Class[0];
    private final Link link;
    private final int rhp;
    private final int csp;
    private final List parameterTypesList;

    public MacroContainerInvokable(final Class[] args, final Link link) {
        super();
        final ArrayList arrayList = new ArrayList();
        int rhp0 = -1;
        int csp0 = -1;
        for (int i = 0; i < args.length; i++) {
            final Class class1 = args[i];
            if (ResultHandler.class.equals(class1))
                rhp0 = i;
            if (CommandSource.class.equals(class1))
                csp0 = i;
            arrayList.add(class1);
        }
        if (rhp0 < 0) {
            rhp0 = arrayList.size();
            arrayList.add(ResultHandler.class);
        }
        if (csp0 < 0) {
            csp0 = arrayList.size();
            arrayList.add(CommandSource.class);
        }
        this.link = link;
        rhp = rhp0;
        csp = csp0;
        parameterTypesList = Collections.unmodifiableList(arrayList);
    }

    public Object invoke(final Object obj, final Object[] args)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, InstantiationException {
        final ResultHandler handler = (ResultHandler) args[rhp];
        for (int i = 0; i < args.length; i++) {
            final Object o = args[i];
            final Class cls = (Class) parameterTypesList.get(i);
            if (!ServiceObject.class.isAssignableFrom(cls)
                    || Englet.class.equals(cls))
                handler.handleResult(o);
        }
        ((CommandSource) args[csp]).start(link);
        return null;
    }

    public Class returnType() {
        return Void.TYPE;
    }

    public Class[] parameterTypes() {
        return (Class[]) parameterTypesList.toArray(A);
    }

    public Class targetType() {
        return null;
    }

}
