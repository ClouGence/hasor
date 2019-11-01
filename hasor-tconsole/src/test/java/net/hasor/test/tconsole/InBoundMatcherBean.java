package net.hasor.test.tconsole;
import java.util.ArrayList;
import java.util.function.Predicate;

public class InBoundMatcherBean extends ArrayList<String> implements Predicate<String> {
    public boolean test(String t) {
        this.add(t);
        return true;
    }
}