package net.hasor.dataql.runtime.ads;
import com.alibaba.fastjson.JSON;
import net.hasor.core.Hasor;
import net.hasor.dataql.*;
import net.hasor.dataql.domain.UdfModel;
import net.hasor.test.dataql.udfs.SqlFragmentUdf;
import org.junit.Test;

import java.io.IOException;

public class RecursionTest extends AbstractTestResource {
    private DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);

    private void queryTest(DataQL dataQL, String testCase) throws IOException {
        Query query = dataQL.createQuery(getScript("/net_hasor_dataql_adv/" + testCase + ".ql"));
        String queryResult = getScript("/net_hasor_dataql_adv/" + testCase + ".result");
        //
        Object unwrap = query.execute().getData().unwrap();
        String jsonData = JSON.toJSONString(unwrap, true);
        assert jsonData.trim().equals(queryResult.trim());
    }

    private void queryTest(String testCase) throws IOException {
        queryTest(this.dataQL, testCase);
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
    public void sql_fragment_test() throws IOException {
        DataQL dataQL1 = Hasor.create().build((QueryModule) apiBinder -> {
            apiBinder.bindFragment("sql", new SqlFragmentUdf(1));
        }).getInstance(DataQL.class);
        queryTest(dataQL1, "sql_fragment");
        //
        DataQL dataQL2 = Hasor.create().build((QueryModule) apiBinder -> {
            apiBinder.bindType("sql", FragmentProcess.class, new SqlFragmentUdf(1));
        }).getInstance(DataQL.class);
        queryTest(dataQL2, "sql_fragment");
    }

    @Test
    public void basic_fmt_test() throws IOException {
        queryTest("basic_fmt");
    }

    @Test
    public void multi_dimensional_test() throws IOException {
        queryTest("multi_dimensional");
    }

    @Test
    public void hints_test() throws IOException {
        queryTest("hints");
    }

    @Test
    public void special_a_test() throws IOException {
        queryTest("special_a");
    }

    @Test
    public void special_b_test() throws IOException {
        queryTest("special_b");
    }

    @Test
    public void special_c_test() throws IOException {
        queryTest("special_c");
    }

    @Test
    public void special_tree_test() throws IOException {
        queryTest("special_tree");
    }
}
