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
    private AppContext appContext;
    @Before
    public void before() {
        this.appContext = Hasor.createAppContext(this);
    }
    @Test
    public void main1() {
        QueryModel queryModel = new GraphQLTest().main1();
        String buildQuery = queryModel.buildQuery();
        this.printTaskTree(buildQuery);
    }
    @Test
    public void main2() {
        QueryModel queryModel = new GraphQLTest().main2();
        String buildQuery = queryModel.buildQuery();
        this.printTaskTree(buildQuery);
    }
    @Test
    public void main3() {
        QueryModel queryModel = new GraphQLTest().main3();
        String buildQuery = queryModel.buildQuery();
        this.printTaskTree(buildQuery);
    }
    @Test
    public void main4() {
        QueryModel queryModel = new GraphQLTest().main4();
        String buildQuery = queryModel.buildQuery();
        this.printTaskTree(buildQuery);
    }
    @Test
    public void main5() {
        QueryModel queryModel = new GraphQLTest().main5();
        String buildQuery = queryModel.buildQuery();
        this.printTaskTree(buildQuery);
    }
    @Test
    public void main6() {
        QueryModel queryModel = new GraphQLTest().main6();
        String buildQuery = queryModel.buildQuery();
        this.printTaskTree(buildQuery);
    }
    @Test
    public void main7() {
        QueryModel queryModel = new GraphQLTest().main7();
        String buildQuery = queryModel.buildQuery();
        this.printTaskTree(buildQuery);
    }
    @Test
    public void main8() {
        QueryModel queryModel = new GraphQLTest().main8();
        String buildQuery = queryModel.buildQuery();
        this.printTaskTree(buildQuery);
    }
    @Test
    public void main9() {
        QueryModel queryModel = new GraphQLTest().main9();
        String buildQuery = queryModel.buildQuery();
        this.printTaskTree(buildQuery);
    }
    //
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        GraphApiBinder binder = apiBinder.tryCast(GraphApiBinder.class);
        binder.addUDF(FindUserByID.class);
        binder.addUDF(QueryOrder.class);
        binder.addUDF(UserManager.class);
        binder.addUDF(Foo.class);
    }
    private void printTaskTree(String buildQuery) {
        //
        GraphContext gc = this.appContext.getInstance(GraphContext.class);
        GraphQuery query = null;
        try {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println(buildQuery);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            query = gc.createQuery(buildQuery);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //
        QueryResult result = query.query(null);
        System.out.println(result);
    }
}