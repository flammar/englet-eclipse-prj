package am.trash;

public class t {

    private static long t;

    /**
     * @param args
     */
    public static void main(final String[] args) {
        start();
        t1();
        System.out.println(System.currentTimeMillis() - t);
        start();
        t2();
        System.out.println(100L * (System.currentTimeMillis() - t));
        start();
        t3();
        System.out.println(100L * (System.currentTimeMillis() - t));
        start();
        t4();
        System.out.println(100L * (System.currentTimeMillis() - t));
        start();
        t5();
        System.out.println((System.currentTimeMillis() - t));
    }

    private static void start() {
        t = System.currentTimeMillis();
    }

    private static void t1() {
        for (int j = 0, i = 0; i < 100000000; i++) {
            ww: {
                j++;
                break ww;
            }
            j++;
        }
    }

    private static void t2() {
        for (int j = 0, i = 0; i < 1000000; i++) {
            try {
                j++;
                throw new Throwable();
            } catch (final Throwable e) {
            }
            j++;
        }
    }

    private static void t3() {
        final Throwable throwable = new Throwable();
        for (int j = 0, i = 0; i < 1000000; i++) {
            try {
                j++;
                throw throwable;
            } catch (final Throwable e) {
            }
            j++;
        }
    }

    private static void t5() {
        for (int j = 0, i = 0; i < 100000000; i++) {
            try {
                j++;
            } catch (final Throwable e) {
            }
            j++;
        }
    }

    private static void t4() {
        final Throwable throwable = new Throwable() {

            public synchronized Throwable fillInStackTrace() {
                return this;
            }

        };
        for (int j = 0, i = 0; i < 1000000; i++) {
            try {
                j++;
                throw throwable;
            } catch (final Throwable e) {
            }
            j++;
        }
    }
}
