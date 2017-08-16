package am.englet.link.backadapters.slider;

import java.util.HashSet;
import java.util.Set;

import am.englet.util.Checker;

public class UniqunessChecker implements Checker {
    Set set = new HashSet();

    public boolean check(final Object o) {
        return set.add(o);
    }

}
