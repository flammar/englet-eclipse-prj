package am.englet;

public abstract class CodeBlock {
    public abstract Object result();

    public String toString() {
        final Object result = result();
        return result != null ? result.toString() : "null";
    }

}
