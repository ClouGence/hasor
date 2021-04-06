package net.hasor.dataql.runtime.qil;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.HintValue;
import net.hasor.dataql.Query;
import org.junit.Test;

public class QilTest extends AbstractTestResource implements HintValue {
    @Test
    public void errorLineTest_1() {
        try {
            Query compilerQL = compilerQL("assert false;");
            compilerQL.execute();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().equalsIgnoreCase("[line 1:7~1:12 ,QIL 0:18] assert test failed.");
            assert e.getLocalizedMessage().equalsIgnoreCase("assert test failed.");
        }
    }
}
