package net.hasor.dataql.runtime.ads;
import com.alibaba.fastjson.JSON;
import net.hasor.core.Hasor;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.Query;
import net.hasor.dataql.domain.UdfModel;
import net.hasor.dataql.extend.binder.DataQL;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class RecursionTest extends AbstractTestResource {
    private DataQL dataQL = Hasor.create().build(apiBinder -> {
        apiBinder.bindType("sql", FragmentProcess.class, (hint, params, fragmentString) -> {
            return fragmentString.trim();
        });
    }).getInstance(DataQL.class);

    private void queryTest1(String testCase) throws IOException {
        Query query = this.dataQL.createQuery(getScript("/net_hasor_dataql_adv/" + testCase + ".ql"));
        String queryResult = getScript("/net_hasor_dataql_adv/" + testCase + ".result");
        //
        Object unwrap = query.execute().getData().unwrap();
        String queryJsonData1 = JSON.toJSONString(unwrap);
        String queryJsonData2 = JSON.toJSONString(JSON.parseObject(queryResult, LinkedHashMap.class));
        assert queryJsonData1.trim().equals(queryJsonData2.trim());
    }

    private void queryTest2(String testCase) throws IOException {
        Query query = this.dataQL.createQuery(getScript("/net_hasor_dataql_adv/" + testCase + ".ql"));
        String queryResult = getScript("/net_hasor_dataql_adv/" + testCase + ".result");
        //
        Object unwrap = query.execute().getData().unwrap();
        String queryJsonData1 = JSON.toJSONString(unwrap);
        String queryJsonData2 = JSON.toJSONString(JSON.parseObject(queryResult, ArrayList.class));
        assert queryJsonData1.trim().equals(queryJsonData2.trim());
    }

    private void queryTest3(String testCase) throws IOException {
        Query query = this.dataQL.createQuery(getScript("/net_hasor_dataql_adv/" + testCase + ".ql"));
        String queryResult = getScript("/net_hasor_dataql_adv/" + testCase + ".result");
        //
        Object unwrap = query.execute().getData().unwrap();
        assert unwrap.equals(queryResult);
    }

    @Test
    public void returnLambda() throws Throwable {
        Query query = this.dataQL.createQuery(getScript("/net_hasor_dataql_adv/return_lambda.ql"));
        UdfModel testUdf = (UdfModel) query.execute().getData();
        //
        assert testUdf.call(new Object[] { 1 }).unwrap().equals("性别：男");
        assert testUdf.call(new Object[] { 0 }).unwrap().equals("性别：女");
    }

    @Test
    public void basic_fmt_test() throws IOException {
        queryTest1("basic_fmt");
    }

    @Test
    public void recursion_test() throws IOException {
        queryTest2("recursion");
    }

    @Test
    public void sql_fragment_test() throws IOException {
        queryTest3("sql_fragment");
    }

    @Test
    public void multi_dimensional_test() throws IOException {
        queryTest2("multi_dimensional");
    }
}