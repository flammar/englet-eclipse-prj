/**
 *
 */
package am.englet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

import am.englet.MethodsStorage.Cast;
import am.englet.link.Link;

// TODO functionality as of layered singleton pool,
// like already made with string-to-object that of variables storage

/**
 * @author Adm1
 * 
 */
public class DataStack implements VariablesStorage, ServiceObject {
    public static boolean DEBUG = false;

    /**
     * @author Adm1
     * 
     */

    public class StackFrame extends HashMap {

        public boolean quiet = false;
        public boolean shading = false;
        final LinkedList shadow = new LinkedList();
        public final Stack st = new Stack();
        StackFrame base = null;
        public int resCount = -1;
        // final int madeAt = main.size();
        // int popLevel = madeAt <= 0 ? -1 : DataStack.this.top().popLevel;
        private ListIterator shitr;

        private static final long serialVersionUID = -8705289629603754945L;

        private StackFrame() {
            super();
        }

        Map sub0(final int n) {
            final Map res = ((n <= 0) || (base == null)) ? new HashMap() : base.sub0(n - 1);
            res.putAll(this);
            return res;
        }

        public StackFrame basedInstance() {
            final StackFrame res = new StackFrame();
            res.base = this.size() > 0 ? this : base;
            return res;
        }

        // public StackFrame() {
        // super();
        // }
        //
        public Object getBased(final Object name) {
            Object res = null;
            for (StackFrame m = this; m != null; m = m.base)
                if (((res = m.get(name)) != null) || m.containsKey(name))
                    break;
            return res;
        }

        // TODO rewrite for better performance
        public boolean has(final Object name) {
            for (StackFrame m = this; m != null; m = m.base)
                if (m.containsKey(name))
                    return true;
            return false;
        }

        public Object peek() {
            return st.peek();
        }

        public Object pop() {
            final Stack stack = st;
            /*
             * final int size3 = stack.size(); if (size3 > 1) return
             * stack.pop(); final DataStack stack1 = DataStack.this; final int
             * size2 = stack1.main.size(); if (size3 > 0) { final Object pop =
             * stack.pop(); popLevel = size2 < 2 ? -1 : stack1.map(size2 -
             * 2).popLevel; return pop; } if (popLevel == madeAt) popLevel =
             * size2 < 2 ? -1 : stack1.map(size2 - 2).popLevel; int i =
             * popLevel; for (; i >= 0; i--) if (stack1.map(i).st.size() > 0)
             * break; popLevel = i; if (popLevel < 0) throw new
             * EmptyStackException(); for (i++; i < size2; i++)
             * stack1.map(i).popLevel = popLevel;
             * 
             * return stack1.map(popLevel).st.pop();
             */
            return stack.pop();
        }

        public Object push(final Object item) {
            /*
             * popLevel = madeAt;
             */return item instanceof ResultList ? ((ResultList) item).appendTo(st) : st.push(Utils.correctValue(item));
        }

        public void set(final Object name, final Object val) {
            StackFrame m = this;
            for (; m != null; m = m.base)
                if (m.containsKey(name))
                    break;
            (m == null ? this : m).put(name, val);
        }

        Object shadow(final Object pop) {
            if (!shading)
                return pop;
            shadow.add(0, pop);
            return pop;
        }

        Object shadow1(final Object pop) {
            if (!shading)
                return pop;
            shitr.add(pop);
            return pop;
        }

        public void shadow1reset() {
            shitr = shadow.listIterator();
        }

        public String toString() {
            return "mapp:" + super.toString() + " base" + (base == null ? '-' : '+') + "stack: " + st;
        }

        // public static StackFrame base(){return new StackFrame();}
    }

    java.util.Stack main;
    private int fp = -1;
    private int sp;
    private boolean faking;
    private boolean popResetMark = false;
    private final ArrayList last = new ArrayList();

    public DataStack() {
        super();
        main = new java.util.Stack();
        frame();
        // main.push( StackFrame.base());
        // main.push( new java.util.Stack());
    }

    private DataStack(final StackFrame frame) {
        super();
        main = new java.util.Stack();
        frame(frame);
        // main.push( StackFrame.base());
        // main.push( new java.util.Stack());
    }

    /**
     * 
     * @see java.util.Vector#clear()
     */
    public void clear() {
        main.clear();
    }

    public void res(final int i) {
        top().resCount = i;
    }

    public void deframe() {
        if (main.size() < 2)
            return;
        final int newSize = main.size() - 1;
        doDeframe(newSize);
    }

    private void doDeframe(final int newSize) {
        final StackFrame top = top();
        final java.util.Stack s = top.st;
        main.setSize(newSize);
        final int size = s.size();
        final Stack stack = stack();
        stack.addAll(top.resCount > -1 ? s.subList(Math.max(0, size - top.resCount), size) : s);
        if (top.shading)
            stack.addAll(top.shadow);
    }

    public void deframeTo(int i) {
        if (i < 2)
            i = 2;
        if (main.size() <= i)
            return;
        doDeframe(i);
    }

    public void enlist() {
        final Stack st1 = stack();
        final List res = Collections.unmodifiableList(new ArrayList(st1));
        st1.setSize(0);
        push(res);
    }

    public ResultList peekResultList() {
        final Stack st1 = stack();
        final ResultList resultList = new ResultList(st1);
        deframe();
        return resultList;
    }

    public ResultList peekResultList(final int n) {
        return new ResultList(stack(), n);
    }

    public StackFrame frame() {
        final int size = main.size();
        final StackFrame basedInstance = size > 0 ? map().basedInstance() : this.new StackFrame();
        // basedInstance.madeAt=size;
        main.push(basedInstance);
        // main.push(new java.util.Stack());
        return basedInstance;
    }

    private void frame(final StackFrame frame) {
        main.push(frame.basedInstance());
    }

    // XXX Obsolete, must cause error. To be removed.
    public void frame(final List l) {
        final StackFrame map = map().basedInstance();
        final ListIterator li = l.listIterator(l.size());
        while (li.hasPrevious())
            map.put(li.previous(), pop());
        main.push(map);
        // main.push(new java.util.Stack());
    }

    public void frame(final Map m) {
        frame();
        top().putAll(m);
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.VariablesStorage#get(java.lang.Object)
     */
    public Object get(final Object name) {
        return top().getBased(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.VariablesStorage#has(java.lang.Object)
     */
    public boolean has(final Object name) {
        return mustNotHave(name) ? false : top().has(name);
    }

    private boolean mustNotHave(final Object name) {
        // TODO Auto-generated method stub
        return name.equals(String.class) || name.equals(Object.class) || name.equals(Link.class)
                || name.equals(Integer.class) || name.equals(int.class) || name.equals(Long.class)
                || name.equals(long.class);
    }

    StackFrame map() {
        return top();
    }

    StackFrame map(final int i) {
        return (StackFrame) main.get(i);
    }

    public Object peek() {
        return top().peek();
    }

    public Object pop() {
        if (popResetMark)
            popResetSelf();
        final StackFrame top = top();
        if (DataStack.DEBUG)
            Utils.outPrintln(System.out, "DataStack.pop()");
        if (top.st.size() > 0)
            return popReg(top.pop());
        if (!top.quiet)
            throw new EmptyStackException();
        if ((fp < 0))
            throw new EmptyStackException();
        while (sp < 0) {
            final StackFrame map = map(--fp);
            if (!map.quiet) {
                popResetSelf();
                throw new EmptyStackException();
            }
            sp = map.st.size() - 1;
        }
        return popReg(pSt().get(sp--));
    }

    private Object popReg(final Object pop) {
        last.add(pop);
        return pop;
    }

    private Stack pSt() {
        return map(fp).st;
    }

    public void popReset() {
        popResetMark = true;
        // popResetSelf();
    }

    private void popResetSelf() {
        popResetMark = false;
        fp = main.size() - 2;
        sp = fp < 0 ? -1 : pSt().size() - 1;
        last.clear();
    }

    public void push(final Object o) {
        top().push(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.VariablesStorage#put(java.lang.Object, java.lang.Object)
     */
    public void put(final Object name, final Object val) {
        map().put(name, val);
    }

    /*
     * (non-Javadoc)
     * 
     * @see am.englet.VariablesStorage#set(java.lang.Object, java.lang.Object)
     */
    public void set(final Object name, final Object val) {
        top().set(name, val);
    }

    public int size() {
        return main.size();
    }

    java.util.Stack stack() {
        return ((StackFrame) main.peek()).st;
    }

    public StackFrame top() {
        return (StackFrame) main.peek();

    }

    public DataStack deframeAll() {
        while (this.size() > 1)
            this.deframe();
        return this;
    }

    public Class[] nTopArgumentTypes(final int n) {
        if (n == 0)
            return new Class[0];
        if (DataStack.DEBUG)
            Utils.outPrintln(System.out, "DataStack.nTopArgumentTypes():n:" + n);
        int len = main.size();
        if (len == 0)
            return new Class[0];
        final Stack st = map(len - 1).st;
        if (DataStack.DEBUG)
            Utils.outPrintln(System.out, "DataStack.nTopArgumentTypes():st:" + st);
        int size = st.size();
        if (DataStack.DEBUG) {
            Utils.outPrintln(System.out, "DataStack.nTopArgumentTypes():len:" + len);
            Utils.outPrintln(System.out, "DataStack.nTopArgumentTypes():size:" + size);
        }
        while ((len > 1) && (size < n) && (map(len - 1)).quiet) {
            final Stack st2 = map(--len - 1).st;
            if (DataStack.DEBUG) {
                Utils.outPrintln(System.out, "DataStack.nTopArgumentTypes():len:" + len);
                Utils.outPrintln(System.out, "DataStack.nTopArgumentTypes():st2:" + st2);
            }
            size += st2.size();
        }
        if (DataStack.DEBUG)
            Utils.outPrintln(System.out, "DataStack.nTopArgumentTypes():size:" + size);
        final Class[] res = new Class[size > n ? n : size];
        // st = map((len = main.size()) - 1).st;
        len = main.size();
        int i = res.length;
        while (i > 0)
            i = putRes(map(len-- - 1).st, res, i);
        // final int z = i;

        // if (DEBUG) {
        // System.out.println("DataStack.nTopArgumentTypes():st:" + st);
        // System.out.println("DataStack.nTopArgumentTypes():len:" + len);
        // }
        // for (int i = res.length, j = st.size() - 1; i-- > 0;) {
        // while ((j < 0) && (len > 0))
        // j = (st = map(len-- - 1).st).size() - 1;
        // if (DEBUG) {
        // System.out.println("DataStack.nTopArgumentTypes():j:" + j);
        // System.out.println("DataStack.nTopArgumentTypes():len:" + len);
        // System.out.println("DataStack.nTopArgumentTypes():i:" + i);
        // }
        // final Object object = st.get(j--);
        // putRes(res, i, object);
        // }
        //

        // int ss = main.size() - 1, i;
        // final Class[] res = new Class[n];
        // for (i = n; (i-- > 0) && (ss >= 0);) {
        // final Stack st = map(ss).st;
        // for (int j = st.size(); (i >= 0) && (j-- > 0);) {
        // final Object object = st.get(j);
        // res[i--] = object != null ? object.getClass() : /* null
        // */Link.class/*
        // * for
        // * null
        // * =
        // * {
        // * }
        // */;
        // }
        // if (i >= 0)
        // while ((ss >= 0) && (map(ss).st.size() == 0))
        // ss--;
        // }
        // if (i >= 0) {
        // final Class[] res1 = new Class[n - 1 - i];
        // System.arraycopy(res, i + 1, res1, 0, res1.length);
        // return res1;
        // }
        if (DataStack.DEBUG)
            Utils.outPrintln(System.out, "DataStack.nTopArgumentTypes():res:" + Arrays.asList(res));
        return res;

        // final int i=
        // return null;
    }

    private int putRes(final Stack st, final Class[] res, int last) {
        for (int j = st.size(); (j-- > 0) && (last > 0);)
            putRes(res, --last, st.get(j));
        return last;
    }

    private void putRes(final Class[] res, final int i, final Object object) {
        res[i] = object != null ? object instanceof Cast ? ((Cast) object).castClass : faking
                && (object instanceof InvokableDescription) ? ((InvokableDescription) object).getInvokable()
                .returnType() : object.getClass() : Link.class/*
                                                               * for null = { }
                                                               */;
        if (DataStack.DEBUG)
            Utils.outPrintln(System.out, "DataStack.putRes(Class[], int, Object):" + Arrays.asList(res) + "[" + i
                    + "]:" + res[i]);
    }

    public void param() {
        final StackFrame top = top();
        final Stack st = top.st;
        final Stack st2 = st2();
        if (!top.quiet)
            while (st.size() > 0)
                top.put(st.pop(), top.shadow(st2.pop()));
        else
            for (int i = st2.size(); st.size() > 0; top.put(st.pop(), top.shadow(st2.get(--i))))
                ;
    }

    private Stack st2() {
        return ((StackFrame) main.get(main.size() - 2)).st;
    }

    public void param(final int n) {
        if (main.size() < 2)
            return;
        final StackFrame top = top();
        final Stack st = top.st;
        final Stack st2 = st2();
        final int size = st2.size();
        // final int n2 = size > n ? n : size;
        final int n0 = size - n;
        top.shadow1reset();
        for (int i = n0; i < size; i++)
            st.push(top.shadow1(st2.get(i)));
        if (!top.quiet)
            st2.setSize(n0);
    }

    public Map sub(final int n) {
        return top().sub0(n);
    }

    public Object at(final int n) {
        return Utils.atStack(stack(), n);
    }

    public Object at(final int m, final int n) {
        final Stack stack = ((StackFrame) Utils.atStack(main, m)).st;
        return Utils.atStack(stack, n);
    }

    public DataStack derive() {
        return new DataStack(top());
    }

    public String toString() {
        return "DataStack [data=" + main + "]";
    }

    public boolean isFaking() {
        return faking;
    }

    public void setFaking(final boolean faking) {
        this.faking = faking;
    }

    public Object last(final int index) {
        return last.get(index);
    }
}
