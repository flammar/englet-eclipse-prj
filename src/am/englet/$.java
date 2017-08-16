/**
 * 14.11.2009
 *
 * 1
 *
 */
package am.englet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author 1
 * 
 */
public class $ extends $Base {
    public static final Map DEPRIMITIIVISATORS;
    public static final Set WRAPPERS;
    static {

        final HashMap m = new HashMap();
        m.put(int.class, Integer.class);
        m.put(long.class, Long.class);
        m.put(byte.class, Byte.class);
        m.put(short.class, Short.class);
        m.put(float.class, Float.class);
        m.put(double.class, Double.class);
        m.put(char.class, Character.class);
        m.put(boolean.class, Boolean.class);
        m.put(void.class, Void.class);
        DEPRIMITIIVISATORS = Collections.unmodifiableMap(m);
        WRAPPERS = Collections.unmodifiableSet(new HashSet(DEPRIMITIIVISATORS
                .values()));
    };

    private static final am.englet.$.Performer FIELD_PERFORMER = new Performer() {

        public Member[] members(final Class cl) {
            return cl.getFields();
        }

        public Member[] declaredMembers(final Class cl) {
            return cl.getDeclaredFields();
        }

        public boolean doCheck(final am.englet.$ l, final Member m) {
            return l.check((Field) m);
        }

        public void doEach(final am.englet.$ l, final Member m) {
            l.each((Field) m);
        }

        public Object doEach(final am.englet.$ l, final Object acc,
                final Member m) {
            return l.each((Field) m, acc);
        }

        public Class[] paramTypes(final Member member) {
            return new Class[] { ((Field) member).getType() };
        }
    };

    private static final am.englet.$.Performer CONSTRUCTOR_PERFORMER = new Performer() {

        public Member[] members(final Class cl) {
            return cl.getConstructors();
        }

        public Object doEach(final $ l, final Object acc, final Member m) {
            return l.each((Constructor) m, acc);
        }

        public void doEach(final $ l, final Member m) {
            l.each((Constructor) m);
        }

        public boolean doCheck(final $ l, final Member m) {
            return l.check((Constructor) m);
        }

        public Member[] declaredMembers(final Class cl) {
            return cl.getDeclaredConstructors();
        }

        public Class[] paramTypes(final Member member) {
            return ((Constructor) member).getParameterTypes();
        }
    };

    static final am.englet.$.Performer METHOD_PERFORMER = new MethodPerformer();

    static final $Performer FIRST_$PERFORMER = new First$Performer();

    private static final $Performer LOOP_$PERFORMER = new $Performer() {
        public Object $(final $ l, final Object acc, final Member[] members2,
                final int filter, final int sample, final Class[] paramTypes,
                final String[] names) {
            l.performer.loop(l, members2, filter, sample, paramTypes, names);
            return null;
        }
    };

    private static final $Performer SUMMARY_$PERFORMER = new $Performer() {
        public Object $(final $ l, final Object acc, final Member[] members2,
                final int filter, final int sample, final Class[] paramTypes,
                final String[] names) {
            return l.performer.summary(l, members2, acc, filter, sample,
                    paramTypes, names);
        }
    };

    static final Map PERFORMERS = Collections.unmodifiableMap(new HashMap() {
        private static final long serialVersionUID = -3800101834139121010L;

        {
            put(Method.class, METHOD_PERFORMER);
            put(Field.class, FIELD_PERFORMER);
            put(Constructor.class, CONSTRUCTOR_PERFORMER);
            put(boolean.class, FIRST_$PERFORMER);
            put(void.class, LOOP_$PERFORMER);
            put(Object.class, SUMMARY_$PERFORMER);
        }
    });

    static final class MethodPerformer extends Performer {
        public Member[] members(final Class cl) {
            return cl.getMethods();
        }

        public Object doEach(final $ l, final Object acc, final Member m) {
            return l.each((Method) m, acc);
        }

        public void doEach(final $ l, final Member m) {
            l.each((Method) m);
        }

        public boolean doCheck(final $ l, final Member m) {
            return l.check((Method) m);
        }

        public Member[] declaredMembers(final Class cl) {
            return cl.getDeclaredMethods();
        }

        public Class[] paramTypes(final Member member) {
            return ((Method) member).getParameterTypes();
        }
    }

    static final class First$Performer implements $Performer {
        public Object $(final $ l, final Object acc, final Member[] members,
                final int filter, final int sample, final Class[] paramTypes,
                final String[] names) {
            return l.performer.first(l, members, filter, sample, paramTypes,
                    names);
        }
    }

    /**
     * @author 1
     * 
     */
    abstract static class Performer {
        public void loop(final $ l, final Member[] members, final int filter,
                int sample, final Class[] paramTypes, final String[] names) {
            sample &= filter;
            for (int i = 0; i < members.length; i++) {
                final Member member = members[i];
                if (precheck(l, filter, sample, paramTypes, member, names))
                    doEach(l, member);
            }
        }

        public Member first(final $ l, final Member[] members,
                final int filter, int sample, final Class[] paramTypes,
                final String[] names) {
            sample &= filter;
            for (int i = 0; i < members.length; i++) {
                final Member m = members[i];
                if (precheck(l, filter, sample, paramTypes, m, names)
                        && doCheck(l, m))
                    return m;
            }
            return null;
        }

        public Object summary(final $ l, final Member[] members2,
                Object accumulator, final int filter, int sample,
                final Class[] paramTypes, final String[] names) {
            sample &= filter;
            for (int i = 0; i < members2.length; i++) {
                final Member m = members2[i];
                if (precheck(l, filter, sample, paramTypes, m, names))
                    accumulator = doEach(l, accumulator, m);
            }
            return accumulator;
        };

        boolean precheck(final $ l, final int filter, final int sample,
                final Class[] paramTypes, final Member member,
                final String[] names) {
            return ((paramTypes == null) || (/*
                                              * .toString()&String.valueOf()
                                              * prevention
                                              */l.cls.equals(String.class) ? $
                    .paramTypesEqual(paramTypes, paramTypes(member)) : $
                    .paramTypesFit(paramTypes, paramTypes(member))))
                    && ((names == null) || nameFits(names, member.getName()))
                    && ((filter == 0) || ((member.getModifiers() & filter) == sample));
        }

        boolean nameFits(final String[] names, final String name) {
            for (int i = 0; i < names.length; i++)
                if (names[i].equals(name))
                    return true;
            return false;
        }

        abstract Class[] paramTypes(Member member);

        abstract Member[] members(Class cl);

        abstract Member[] declaredMembers(Class cl);

        abstract void doEach($ l, Member m);

        abstract boolean doCheck($ l, Member m);

        abstract Object doEach($ l, Object acc, Member m);
    }

    interface $Performer {
        public Object $($ l, Object acc, Member[] members2, int filter,
                int sample, Class[] paramTypes, String[] names);
    }

    public class Declared extends $Base {
        public Object $(final Object acc, final int filter, final int sample,
                final Class[] paramTypes, final String[] names) {
            return $performer.$($.this, acc, declaredMembers(), filter, sample,
                    paramTypes, names);
        }
    }

    final Class cls;
    public final Declared declared = this.new Declared();
    final Performer performer;
    final $Performer $performer;

    // private final Method declaredMethod;

    public $(final Class cls) {
        this.cls = cls;
        final Method declaredMethod = getClass().getDeclaredMethods()[0];
        performer = (Performer) PERFORMERS.get(declaredMethod
                .getParameterTypes()[0]);
        $performer = ($Performer) PERFORMERS
                .get(declaredMethod.getReturnType());
    }

    public $(final Class cls, final Class memberType) {
        this.cls = cls;
        performer = (Performer) PERFORMERS.get(memberType);
        $performer = ($Performer) PERFORMERS.get(boolean.class);
    }

    final public Object $(final Object acc, final int filter, final int sample,
            final Class[] paramTypes, final String[] names) {
        return $performer.$(this, acc, members(), filter, sample, paramTypes,
                names);
    }

    private Member[] declaredMembers() {
        return performer.declaredMembers(cls);
    }

    Member[] members() {
        return performer.members(cls);
    }

    public void each(final Method mtd) {
    }

    public Object each(final Method mtd, final Object acc) {
        return acc;
    }

    public boolean check(final Method mtd) {
        return getClass().equals($.class);
    }

    public void each(final Field fld) {
    }

    public Object each(final Field fld, final Object acc) {
        return acc;
    }

    public boolean check(final Field fld) {
        return getClass().equals($.class);
    }

    public void each(final Constructor con) {
    }

    public Object each(final Constructor con, final Object acc) {
        return acc;
    }

    public boolean check(final Constructor con) {
        return getClass().equals($.class);
    }

    static boolean paramTypesEqual(final Class[] paramTypes,
            final Class[] paramTypes2) {
        final int length = paramTypes.length;
        if (length != paramTypes2.length)
            return false;
        else
            for (int i = 0; i < paramTypes2.length; i++) {
                final Class class1 = paramTypes[i];
                // if null provided then do not check <= BSH
                final Class class2 = paramTypes2[i];
                if ((class1 != null) && !class2.equals(class1))
                    return false;
            }
        return true;
    }

    static boolean paramTypesFit(final Class[] paramTypes,
            final Class[] paramTypes2) {
        final int length = paramTypes.length;
        if (length != paramTypes2.length)
            return false;
        else
            for (int i = 0; i < paramTypes2.length; i++) {
                final Class class1 = paramTypes[i];
                // if null provided then do not check <= BSH
                final Class class2 = paramTypes2[i];
                if ((class1 != null) && // !class2.isAssignableFrom(class1)
                        !Utils.isCastable(class2, class1))
                    return false;
            }
        return true;
    }

    // private static Comparator classComparator() {
    // return new Comparator() {
    //
    // public int compare(final Object o1, final Object o2) {
    // final Class c1 = (Class) o1, c2 = (Class) o2;
    // if (isMoreGeneralThan(c2, c1))
    // return -1;
    // if (isMoreGeneralThan(c1, c2))
    // return 1;
    // return 0;
    // }
    // };
    // }

    public static boolean isMoreGeneralThan(final Class class1,
            final Class class2) {
        return class1.isAssignableFrom(class2)
                || class1.equals(Object.class)
                || ($.WRAPPERS.contains(class2) && (!$.WRAPPERS
                        .contains(class1)))
                || Utils.isUpCastable(class1, class2);
    }
}

abstract class $Base {
    public abstract Object $(final Object acc, final int filter,
            final int sample, Class[] paramTypes, String[] names);

    public Object $(final Object acc, final int filter, final int sample,
            final Class[] paramTypes) {
        return $(acc, filter, sample, paramTypes, null);
    }

    public Object $(final Object acc, final int filter, final int sample,
            final String[] names) {
        return $(acc, filter, sample, null, names);
    }

    public Object $(final Object acc, final int filter, final int sample) {
        return $(acc, filter, sample, null, null);
    }

    final public Object $(final Object acc, final Class[] paramTypes,
            final String[] names) {
        return $(acc, 0, 0, paramTypes, names);
    }

    final public Object $(final Object acc, final Class[] paramTypes) {
        return $(acc, 0, 0, paramTypes, null);
    }

    final public Object $(final Object acc, final String[] names) {
        return $(acc, 0, 0, null, names);
    }

    final public Object $(final Object acc) {
        return $(acc, 0, 0, null, null);
    }

    final public Object $(final int filter, final int sample,
            final Class[] paramTypes, final String[] names) {
        return $(null, filter, sample, paramTypes, names);
    }

    final public Object $(final int filter, final int sample,
            final Class[] paramTypes) {
        return $(null, filter, sample, paramTypes, null);
    }

    final public Object $(final int filter, final int sample,
            final String[] names) {
        return $(null, filter, sample, null, names);
    }

    final public Object $(final int filter, final int sample) {
        return $(null, filter, sample, null, null);
    }

    final public Object $(final Class[] paramTypes, final String[] names) {
        return $(null, paramTypes, names);
    }

    final public Object $(final Class[] paramTypes) {
        return $(null, paramTypes);
    }

    final public Object $(final String[] names) {
        return $(null, (Class[]) null, names);
    }

    final public Object $() {
        return $(null, (Class[]) null);
    }

    final public Field field() {
        return (Field) $();
    }

    final public Field field(final String[] names) {
        return (Field) $(names);
    }

    final public Field field(final Class cl) {
        return (Field) $(new Class[] { cl });
    }

    final public Field field(final Class cl, final String[] names) {
        return (Field) $(new Class[] { cl }, names);
    }

    final public Field field(final int filter, final int sample, final Class cl) {
        return (Field) $(filter, sample, new Class[] { cl });
    }

    final public Field field(final int filter, final int sample,
            final Class cl, final String[] names) {
        return (Field) $(filter, sample, new Class[] { cl }, names);
    }

    final public Field field(final int filter, final int sample) {
        return (Field) $(filter, sample);
    }

    final public Field field(final int filter, final int sample,
            final String[] names) {
        return (Field) $(filter, sample, names);
    }

    final public Constructor constructor(final Class[] cl) {
        return (Constructor) $(cl);
    }

    final public Constructor constructor() {
        return (Constructor) $();
    }

    final public Constructor constructor(final int filter, final int sample,
            final Class[] cl) {
        return (Constructor) $(filter, sample, cl);
    }

    final public Constructor constructor(final int filter, final int sample) {
        return (Constructor) $(filter, sample);
    }

    final public Method method(final Class[] cl) {
        return (Method) $(cl);
    }

    final public Method method() {
        return (Method) $();
    }

    final public Method method(final int filter, final int sample,
            final Class[] cl) {
        return (Method) $(filter, sample, cl);
    }

    final public Method method(final int filter, final int sample) {
        return (Method) $(filter, sample);
    }

    final public Method method(final Class[] cl, final String[] names) {
        return (Method) $(cl, names);
    }

    final public Method method(final String[] names) {
        return (Method) $(names);
    }

    final public Method method(final int filter, final int sample,
            final Class[] cl, final String[] names) {
        return (Method) $(filter, sample, cl, names);
    }

    final public Method method(final int filter, final int sample,
            final String[] names) {
        return (Method) $(filter, sample, names);
    }
}
