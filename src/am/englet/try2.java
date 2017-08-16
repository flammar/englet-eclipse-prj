package am.englet;

import java.util.Arrays;
import java.util.List;

public class try2 {
    static class A {
        void p() {
            final List x = Arrays.asList(this.getClass().getDeclaredMethods());
            System.out.println(x);
        }

        void a() {
        }
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        new A().p();
        new A() {
        }.p();
        new A() {
            void a() {
            }
        }.p();
    }

}
