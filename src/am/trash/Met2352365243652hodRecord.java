package am.trash;

public abstract class Met2352365243652hodRecord {

    protected final Object method;

    public static class Type {
        public static final int PROCESSING = 0;
        public static final int MANAGEMENT = PROCESSING + 1;
        public static final int IMMEDIATE  = MANAGEMENT + 1;

        public static final String toString(final int i) {
            return i == PROCESSING ? "PROCESSING"
                    : i == MANAGEMENT ? "MANAGEMENT"
                            : i == IMMEDIATE ? "IMMEDIATE" : "";
        }
    }

    public final int     type;
    public final boolean isVoid;
    public final int     argsCount;
    public final boolean isStatic;
    public final Class   targetType;
    public final Class   resultType;
    public final Class[] argTypes;

    public Met2352365243652hodRecord(final Object method, final int type,
            final boolean isVoid, final int argsCount, final boolean isStatic,
            final Class targetType, final Class resultType,
            final Class[] argTypes) {
        this.method = method;
        this.type = type;
        this.isVoid = isVoid;
        this.argsCount = argsCount;
        this.isStatic = isStatic;
        this.targetType = targetType;
        this.resultType = resultType;
        this.argTypes = argTypes;
    }

    public final boolean isProcessing() {
        return type == Type.PROCESSING;
    }

    public final boolean isImmediate() {
        return type == Type.IMMEDIATE;
    }

    /**
     * @return
     * @see java.lang.reflect.Method#getParameterTypes()
     */
    public Class[] argTypes() {
        return argTypes;
    }

    public String toString() {
        return Type.toString(type) + " " + method.toString();
    }

}