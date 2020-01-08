package net.hasor.dataql.extend;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.dataql.*;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ListModel;
import net.hasor.dataql.domain.ValueModel;
import net.hasor.dataql.runtime.InstructRuntimeException;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

public class HasorTest {
    @Test
    public void hasor_0() throws IOException, InstructRuntimeException {
        AppContext appContext = Hasor.create().build((QueryModule) apiBinder -> {
            apiBinder.addShareVarInstance("abc", true);
            apiBinder.addShareVarInstance("bcd", false);
        });
        //
        DataQL dataQL = appContext.getInstance(DataQL.class);
        QueryResult queryResult = dataQL.createQuery("return userByID({'id': 4}) => {\n" + "    'name',\n" + "    'sex' : (sex == 'F') ? '男' : '女' ,\n" + "    'age' : age + '岁'\n" + "}").execute();
        //
        DataModel dataModel = queryResult.getData();
        assert dataModel.isListModel();
        assert ((ListModel) dataModel).asValueModel(0).asBoolean();
        assert !((ListModel) dataModel).asValueModel(1).asBoolean();
    }

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
            Udf testUdf = (readOnly, params) -> readOnly.getHint("abc");
            DataApiBinder dataApiBinder = apiBinder.tryCast(DataApiBinder.class);
            //
            dataApiBinder.addShareVar("foo", () -> testUdf);
            dataApiBinder.setHint("abc", "aaaa");
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
        query.setHint("abc", "bbb");
        queryResult = query.execute();
        dataModel = queryResult.getData();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asString().equals("bbb");
    }
}
