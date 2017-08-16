package am.englet;

import java.util.List;

/**
 * @author 1
 * 
 */
public class ResultList {

    private final Object[] content;

    /**
	 *
	 */
    public ResultList(final List list) {
        content = list.toArray();
    }

    public ResultList(final Object[] list) {
        content = Utils.copy(list);
    }

    /**
	 *
	 */
    public ResultList(final List list, final int n) {
        final int size = list.size();
        content = list.subList(size - n, size).toArray();
    }

    public ResultList appendTo(final List list) {
        for (int i = 0; i < content.length; i++)
            list.add(Utils.correctValue(content[i]));
        return this;
    }

    public Object[] content() {
        return Utils.copy(content);
    }
}