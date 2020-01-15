package net.hasor.dataql.runtime.basic;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.Finder;
import net.hasor.dataql.HintValue;
import net.hasor.dataql.Query;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ListModel;
import net.hasor.dataql.domain.UdfModel;
import net.hasor.dataql.domain.ValueModel;
import net.hasor.test.dataql.udfs.DemoUdf;
import org.junit.Test;

public class LambdaRuntimeTest extends AbstractTestResource implements HintValue {
    @Test
    public void lambda_1_Test() throws Exception {
        Query compilerQL = compilerQL("var sex_str = (sex) -> return (sex == 'F') ? '男' : '女' ; return [sex_str(${_0}),sex_str(${_1})]");
        DataModel dataModel = compilerQL.execute(new Object[] { "F", "M" }).getData();
        assert dataModel.isListModel();
        assert ((ListModel) dataModel).asValueModel(0).asString().equals("男");
        assert ((ListModel) dataModel).asValueModel(1).asString().equals("女");
    }

    @Test
    public void lambda_2_Test() throws Exception {
        Query compilerQL = compilerQL("var a = 10 ; var foo = () -> return a ; return foo()");
        DataModel dataModel = compilerQL.execute().getData();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asInt() == 10;
    }

    @Test
    public void lambda_3_Test() throws Exception {
        Query compilerQL = compilerQL("var a = 10 ; var foo = () -> { var a = 12; return a; } ; return foo()");
        DataModel dataModel = compilerQL.execute().getData();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asInt() == 12;
    }

    @Test
    public void lambda_4_Test() throws Exception {
        Query compilerQL = compilerQL("var a = 10 ; var foo = () -> { return a; } ; var a = 12; return foo()");
        DataModel dataModel = compilerQL.execute().getData();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asInt() == 12;
    }

    @Test
    public void lambda_5_Test() throws Exception {
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(DemoUdf.class).idWith(DemoUdf.class.getName());
        });
        Finder finder = new Finder() {
            @Override
            public Object findBean(String beanName) {
                return appContext.getInstance(beanName);
            }
        };
        //
        Query compilerQL = compilerQL("import 'net.hasor.test.dataql.udfs.DemoUdf' as foo; return foo().name", finder);
        DataModel dataModel = compilerQL.execute().getData();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asString().equals("马三");
    }

    @Test
    public void lambda_5_1_Test() throws Exception {
        AppContext appContext = Hasor.create().build(apiBinder -> {
        });
        Finder finder = new Finder() {
            @Override
            public Object findBean(String beanName) {
                return appContext.getInstance(beanName);
            }
        };
        //
        Query compilerQL = compilerQL("import 'net.hasor.test.dataql.udfs.DemoUdf' as foo; return foo().name", finder);
        DataModel dataModel = compilerQL.execute().getData();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asString().equals("马三");
    }

    @Test
    public void lambda_6_Test() throws Throwable {
        Query compilerQL = compilerQL("var a = 10 ; var foo = () -> { return a; } ; return foo");
        DataModel dataModel = compilerQL.execute().getData();
        assert dataModel.isUdfModel();
        DataModel dat = ((UdfModel) dataModel).call(null, null);
        assert dat.isValueModel();
        assert ((ValueModel) dat).asInt() == 10;
    }
}