package net.hasor.dataql.runtime.basic;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.HintValue;
import net.hasor.dataql.Query;
import org.junit.Test;

import java.io.IOException;

public class AssertRuntimeTest extends AbstractTestResource implements HintValue {
    @Test
    public void assert_1_Test() throws Exception {
        Query compilerQL = compilerQL("assert true;");
        compilerQL.execute();
        assert true;
    }

    @Test
    public void assert_2_Test() {
        try {
            Query compilerQL = compilerQL("assert false;");
            compilerQL.execute();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().contains("assert test failed.");
            assert e.getLocalizedMessage().equalsIgnoreCase("assert test failed.");
        }
    }

    @Test
    public void assert_3_Test() throws IOException {
        compilerQL("assert 1 == 1;").execute();
        compilerQL("assert 1 != 2;").execute();
        assert true;
    }

    @Test
    public void assert_4_Test() {
        try {
            Query compilerQL = compilerQL("assert 12;");
            compilerQL.execute();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().contains("assert expression value is not 'boolean' type.");
            assert e.getLocalizedMessage().equalsIgnoreCase("assert expression value is not 'boolean' type.");
        }
    }

    @Test
    public void assert_5_Test() throws IOException {
        try {
            Query compilerQL = compilerQL("var booleanValue = () -> return 1 ; assert booleanValue();");
            compilerQL.execute();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().contains("assert expression value is not 'boolean' type.");
            assert e.getLocalizedMessage().equalsIgnoreCase("assert expression value is not 'boolean' type.");
        }
        //
        Query compilerQL = compilerQL("var booleanValue = () -> return true ; assert booleanValue();");
        compilerQL.execute();
        assert true;
    }
}
