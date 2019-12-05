package net.hasor.dataql.runtime.basic;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.OptionValue;
import net.hasor.dataql.Query;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.dataql.domain.ValueModel;
import org.junit.Test;

public class ExitRuntimeTest extends AbstractTestResource implements OptionValue {
    @Test
    public void return_1_Test() throws Exception {
        Query compilerQL = compilerQL("return 12 ,true + false;");
        QueryResult result = compilerQL.execute();
        //
        DataModel dataModel = result.getData();
        assert result.getCode() == 12;
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asString().equals("truefalse");
        assert !result.isThrow();
    }

    @Test
    public void return_2_Test() throws Exception {
        Query compilerQL = compilerQL("return 12 ,{};");
        QueryResult result = compilerQL.execute();
        //
        DataModel dataModel = result.getData();
        assert result.getCode() == 12;
        assert dataModel.isObjectModel();
        assert ((ObjectModel) dataModel).size() == 0;
        assert !result.isThrow();
    }

    @Test
    public void exit_1_Test() throws Exception {
        Query compilerQL = compilerQL("exit 12 ,true + false; exit false;");
        QueryResult result = compilerQL.execute();
        //
        DataModel dataModel = result.getData();
        assert result.getCode() == 12;
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asString().equals("truefalse");
        assert !result.isThrow();
    }

    @Test
    public void exit_2_Test() throws Exception {
        Query compilerQL = compilerQL("exit 12 ,{}; exit false;");
        QueryResult result = compilerQL.execute();
        //
        DataModel dataModel = result.getData();
        assert result.getCode() == 12;
        assert dataModel.isObjectModel();
        assert ((ObjectModel) dataModel).size() == 0;
        assert !result.isThrow();
    }

    @Test
    public void throw_1_Test() throws Exception {
        Query compilerQL = compilerQL("throw 12 ,true + false;");
        QueryResult result = compilerQL.execute();
        //
        DataModel dataModel = result.getData();
        assert result.getCode() == 12;
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asString().equals("truefalse");
        assert result.isThrow();
    }

    @Test
    public void throw_2_Test() throws Exception {
        Query compilerQL = compilerQL("throw 12 ,{}; exit false;");
        QueryResult result = compilerQL.execute();
        //
        DataModel dataModel = result.getData();
        assert result.getCode() == 12;
        assert dataModel.isObjectModel();
        assert ((ObjectModel) dataModel).size() == 0;
        assert result.isThrow();
    }
}