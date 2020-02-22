package net.hasor.dataql.fx.script;
import com.alibaba.fastjson.JSON;
import net.hasor.core.Hasor;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.Query;
import net.hasor.dataql.fx.AbstractTestResource;
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
    public void recursion_test() throws IOException {
        queryTest("recursion");
    }

    @Test
    public void totree_test() throws IOException {
        queryTest("totree");
    }

    @Test
    public void mapjoin_test() throws IOException {
        queryTest("mapjoin");
    }
}
