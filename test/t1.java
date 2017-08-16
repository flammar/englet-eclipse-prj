public class t1 {
    static int s = 1;

    public static void main(final String[] args) {
        System.out.println(s());
        ;
        System.out.println(s());
        ;
    }

    private static int s() {
        try {
            return s;
        } finally {
            s = 0;
        }
    }
}
