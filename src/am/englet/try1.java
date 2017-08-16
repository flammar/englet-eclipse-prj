/**
 * 
 */
package am.englet;

/**
 * @author Adm1
 * 
 */
public class try1 implements Cloneable {
    public class try2 implements Cloneable {
        {
            Utils.outPrintln(System.out, "inner con hello");
        }

        protected Object clone() throws CloneNotSupportedException {
            Utils.outPrintln(System.out, "inner clone hello");
            return super.clone();
        }

        public try1 try1() {
            return try1.this;
        }

    }

    public static void main(final String[] args) throws Exception {
        // new try2();
        final try2 a = new am.englet.try1().new try2(), b = new am.englet.try1().new try2();
        b.try1().bb.append(1);
        a.try1().bb.append(2);
        System.out.println(a.try1().bb);
        System.out.println(((try2) a.clone()).try1().bb);
        System.out.println(b.try1().bb);
        final try1 t1 = new try1();
        t1.clone();
    }

    {
        Utils.outPrintln(System.out, "outer con hello");
    }
    public StringBuffer bb = new StringBuffer();

    protected Object clone() throws CloneNotSupportedException {
        Utils.outPrintln(System.out, "outer clone hello");
        return super.clone();
    }

}
