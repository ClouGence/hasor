package net.hasor.dataql.extend;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.dataql.Query;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.UDF;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ListModel;
import net.hasor.dataql.domain.ValueModel;
import net.hasor.dataql.extend.binder.DataApiBinder;
import net.hasor.dataql.extend.binder.DataQL;
import net.hasor.dataql.runtime.InstructRuntimeException;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

public class HasorTest {
    @Test
    public void hasor_1() throws IOException, InstructRuntimeException {
        AppContext appContext = Hasor.create().build();
        DataQL dataQL = appContext.getInstance(DataQL.class);
        QueryResult queryResult = dataQL.createQuery("var a= 10 ; return a").execute();
        //
        DataModel dataModel = queryResult.getData();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asInt() == 10;
    }

    @Test
    public void hasor_2() throws IOException, InstructRuntimeException {
        AppContext appContext = Hasor.create().build();
        DataQL dataQL = appContext.getInstance(DataQL.class);
        //
        HashMap<String, Object> tempData = new HashMap<String, Object>() {{
            put("uid", "uid form tempData");
            put("sid", "sid form tempData");
        }};
        //
        QueryResult queryResult = dataQL.createQuery("return [${uid},${sid}]").execute(tempData);
        DataModel dataModel = queryResult.getData();
        assert dataModel.isListModel();
        assert ((ListModel) dataModel).asValueModel(0).asString().equals("uid form tempData");
        assert ((ListModel) dataModel).asValueModel(1).asString().equals("sid form tempData");
    }

    @Test
    public void hasor_3() throws IOException, InstructRuntimeException {
        AppContext appContext = Hasor.create().build(apiBinder -> {
            UDF testUdf = (params, readOnly) -> readOnly.getOption("abc");
            DataApiBinder dataApiBinder = apiBinder.tryCast(DataApiBinder.class);
            //
            dataApiBinder.addShareVar("foo", () -> testUdf);
            dataApiBinder.setOption("abc", "aaaa");
        });
        DataQL dataQL = appContext.getInstance(DataQL.class);
        //
        Query query = dataQL.createQuery("return foo()");
        QueryResult queryResult = query.execute();
        DataModel dataModel = queryResult.getData();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asString().equals("aaaa");
        //
        //
        query.setOption("abc", "bbb");
        queryResult = query.execute();
        dataModel = queryResult.getData();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asString().equals("bbb");
    }
}
