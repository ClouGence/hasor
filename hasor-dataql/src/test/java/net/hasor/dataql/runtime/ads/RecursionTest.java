package net.hasor.dataql.runtime.ads;
import net.hasor.core.Hasor;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.Finder;
import net.hasor.dataql.Query;
import net.hasor.dataql.UDF;
import net.hasor.dataql.compiler.QueryModel;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.domain.UdfModel;
import net.hasor.dataql.extend.binder.DataQL;
import net.hasor.dataql.runtime.InstructRuntimeException;
import net.hasor.dataql.runtime.QueryHelper;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RecursionTest extends AbstractTestResource {
    @Test
    public void recursion() throws IOException, InstructRuntimeException {
        ArrayList<Object> finalData = new ArrayList<>();
        UDF addToArray = (params, readOnly) -> {
            finalData.add(params[0]);
            return null;
        };
        //
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        String recursionScript = getScript("/net_hasor_dataql_adv/recursion.ql");
        Query query = dataQL.createQuery(recursionScript);
        query.execute(new HashMap<String, Object>() {{
            put("addToArray", addToArray);
        }});
        //
        assert finalData.size() == 10;
    }

    @Test
    public void returnLambda() throws Throwable {
        DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
        String recursionScript = getScript("/net_hasor_dataql_adv/return_lambda.ql");
        Query query = dataQL.createQuery(recursionScript);
        UdfModel testUdf = (UdfModel) query.execute().getData();
        //
        assert testUdf.call(new Object[] { 1 }).unwrap().equals("性别：男");
        assert testUdf.call(new Object[] { 0 }).unwrap().equals("性别：女");
    }

    @Test
    public void t1111() throws Throwable {
        String query1 = "var foo = (a,b,c) -> return {\"name\" : a}; return foo().name";
        QueryModel queryModel = QueryHelper.queryParser(query1);
        QIL qil = QueryHelper.queryCompiler(queryModel, null, Finder.DEFAULT);
        //
        System.out.println(query1);
        System.out.println(qil);
    }
}