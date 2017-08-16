package am.englet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class LoopTest extends TestCase {

    public void test1() {
        final List l = new ArrayList();
        new $(String.class) {

            public void each(final Field fld) {
                l.add(fld);
            }

        }.declared.$();
        System.out.println(l);

        System.out.println(new $(String.class) {
            public boolean check(final Method mtd) {
                return mtd.getName().indexOf("Of") > 0;
            }
        }.$());
        System.out.println(new $(String.class) {
            public boolean check(final Method mtd) {
                return mtd.getName().indexOf("Of") > 0;
            }
        }.$(Modifier.STATIC, 0));
    }
}
