package net.test.hasor.graphql;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.graphql.GraphApiBinder;
import net.hasor.graphql.GraphQuery;
import net.hasor.graphql.QueryResult;
import net.hasor.graphql.ctx.GraphContext;
import net.hasor.graphql.dsl.QueryModel;
import net.hasor.graphql.runtime.QueryTask;
import net.hasor.graphql.runtime.TaskParser;
import net.test.hasor.graphql.udfs.FindUserByID;
import net.test.hasor.graphql.udfs.Foo;
import net.test.hasor.graphql.udfs.QueryOrder;
import net.test.hasor.graphql.udfs.UserManager;
import org.junit.Before;
import org.junit.Test;
/**
 * Created by yongchun.zyc on 2017/3/21.
 */
public class CallTaskTest implements Module {
    private static AppContext appContext;
    @Before
    public void before() {
        if (appContext == null) {
            appContext = Hasor.createAppContext(this);
        }
    }
    @Test
    public void main1() {
        this.printTaskTree(new GraphQLTest().main1());
    }
    @Test
    public void main2() {
        this.printTaskTree(new GraphQLTest().main2());
    }
    @Test
    public void main3() {
        this.printTaskTree(new GraphQLTest().main3());
    }
    @Test
    public void main4() {
        this.printTaskTree(new GraphQLTest().main4());
    }
    @Test
    public void main5() {
        this.printTaskTree(new GraphQLTest().main5());
    }
    @Test
    public void main6() {
        this.printTaskTree(new GraphQLTest().main6());
    }
    @Test
    public void main7() {
        this.printTaskTree(new GraphQLTest().main7());
    }
    @Test
    public void main8() {
        this.printTaskTree(new GraphQLTest().main8());
    }
    @Test
    public void main9() {
        this.printTaskTree(new GraphQLTest().main9());
    }
    //
    // --------------------------------------------------------------------------------------------
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // - UDF
        GraphApiBinder binder = apiBinder.tryCast(GraphApiBinder.class);
        binder.addUDF(FindUserByID.class);
        binder.addUDF(QueryOrder.class);
        binder.addUDF(UserManager.class);
        binder.addUDF(Foo.class);
    }
    private void printTaskTree(QueryModel queryModel) {
        String buildQuery = queryModel.buildQuery();
        // - 执行计划
        {
            QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println(queryTask.printTaskTree(true));
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println(queryTask.printTaskTree(false));
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        // - 执行 QL
        try {
            GraphContext gc = appContext.getInstance(GraphContext.class);
            GraphQuery query = gc.createQuery(buildQuery);
            QueryResult result = query.query(null);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}