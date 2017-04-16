package net.test.hasor.graphql;
import net.hasor.graphql.TaskContext;
import net.hasor.graphql.UDF;
import net.hasor.graphql.dsl.GraphQuery;
import net.hasor.graphql.runtime.AbstractQueryTask;
import net.hasor.graphql.runtime.QueryTask;
import net.hasor.graphql.runtime.TaskParser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by yongchun.zyc on 2017/3/21.
 */
public class CallTaskTest {
    @Test
    public void main1() {
        GraphQuery graphQuery = new GraphQLTest().main1();
        QueryTask queryTask = new TaskParser().doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main2() {
        GraphQuery graphQuery = new GraphQLTest().main2();
        QueryTask queryTask = new TaskParser().doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main3() {
        GraphQuery graphQuery = new GraphQLTest().main3();
        QueryTask queryTask = new TaskParser().doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main4() {
        GraphQuery graphQuery = new GraphQLTest().main4();
        QueryTask queryTask = new TaskParser().doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main5() {
        GraphQuery graphQuery = new GraphQLTest().main5();
        QueryTask queryTask = new TaskParser().doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main6() {
        GraphQuery graphQuery = new GraphQLTest().main6();
        QueryTask queryTask = new TaskParser().doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main7() {
        GraphQuery graphQuery = new GraphQLTest().main7();
        QueryTask queryTask = new TaskParser().doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main8() {
        GraphQuery graphQuery = new GraphQLTest().main8();
        QueryTask queryTask = new TaskParser().doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main9() {
        GraphQuery graphQuery = new GraphQLTest().main9();
        QueryTask queryTask = new TaskParser().doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    //
    private TaskContext newTaskContext() {
        final UDF findUserByID = new UDF() {
            @Override
            public Object call(Map<String, Object> values) {
                HashMap<String, Object> udfData = new HashMap<String, Object>();
                udfData.put("name2", "this is name2.");
                udfData.put("age", 31);
                udfData.put("nick", "my name is nick.");
                udfData.put("userID", 12345);
                udfData.put("status", true);
                return udfData;
            }
        };
        final UDF queryOrder = new UDF() {
            @Override
            public Object call(Map<String, Object> values) {
                ArrayList<Object> orderList = new ArrayList<Object>();
                for (int i = 0; i < 10; i++) {
                    HashMap<String, Object> udfData = new HashMap<String, Object>();
                    udfData.put("accountID", 123);
                    udfData.put("orderID", 123456789);
                    udfData.put("itemID", 987654321);
                    udfData.put("itemName", "商品名称");
                    orderList.add(udfData);
                }
                return orderList;
            }
        };
        final UDF userManager = new UDF() {
            @Override
            public Object call(Map<String, Object> values) {
                HashMap<String, Object> udfData = new HashMap<String, Object>();
                udfData.put("userID", 12345);
                udfData.put("age", 31);
                udfData.put("nick", "my name is nick.");
                udfData.put("name", "this is name2.");
                udfData.put("status", true);
                return udfData;
            }
        };
        final UDF foo = new UDF() {
            @Override
            public Object call(Map<String, Object> values) {
                return 12345;
            }
        };
        //
        return new TaskContext() {
            @Override
            public UDF findUDF(String udfName) {
                if ("userManager.findUserByID".equalsIgnoreCase(udfName))
                    return userManager;
                if ("queryOrder".equalsIgnoreCase(udfName))
                    return queryOrder;
                if ("findUserByID".equalsIgnoreCase(udfName))
                    return findUserByID;
                if ("foo".equalsIgnoreCase(udfName))
                    return foo;
                return null;
            }
            @Override
            public Object get(String name) {
                return "env param " + name;
            }
        };
    }
    private void printTaskTree(QueryTask queryTask) {
        TaskContext tc = newTaskContext();
        AbstractQueryTask task = (AbstractQueryTask) queryTask;
        List<AbstractQueryTask> allTask = task.getAllTask();
        int runCount = 0;
        do {
            runCount = 0;
            for (AbstractQueryTask t : allTask) {
                if (t.isWaiting()) {
                    t.run(tc, null);
                    runCount++;
                }
            }
        } while (runCount != 0);
        //
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(queryTask.printTaskTree(true));
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(queryTask.printTaskTree(false));
        //
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Object value = null;
        try {
            value = queryTask.getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        System.out.println(value);
    }
}