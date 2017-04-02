package net.test.hasor.graphql;
import net.hasor.graphql.TaskContext;
import net.hasor.graphql.UDF;
import net.hasor.graphql.dsl.GraphQuery;
import net.hasor.graphql.task.AbstractQueryTask;
import net.hasor.graphql.task.QueryTask;
import net.hasor.graphql.task.TaskParser;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by yongchun.zyc on 2017/3/21.
 */
public class TaskTest {
    @Test
    public void main1() {
        GraphQuery graphQuery = new GraphQLTest().main1();
        QueryTask queryTask = new TaskParser(newTaskContext()).doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main2() {
        GraphQuery graphQuery = new GraphQLTest().main2();
        QueryTask queryTask = new TaskParser(newTaskContext()).doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main3() {
        GraphQuery graphQuery = new GraphQLTest().main3();
        QueryTask queryTask = new TaskParser(newTaskContext()).doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main4() {
        GraphQuery graphQuery = new GraphQLTest().main4();
        QueryTask queryTask = new TaskParser(newTaskContext()).doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main5() {
        GraphQuery graphQuery = new GraphQLTest().main5();
        QueryTask queryTask = new TaskParser(newTaskContext()).doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main6() {
        GraphQuery graphQuery = new GraphQLTest().main6();
        QueryTask queryTask = new TaskParser(newTaskContext()).doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main7() {
        GraphQuery graphQuery = new GraphQLTest().main7();
        QueryTask queryTask = new TaskParser(newTaskContext()).doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main8() {
        GraphQuery graphQuery = new GraphQLTest().main8();
        QueryTask queryTask = new TaskParser(newTaskContext()).doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main9() {
        GraphQuery graphQuery = new GraphQLTest().main9();
        QueryTask queryTask = new TaskParser(newTaskContext()).doParser(graphQuery.getDomain());
        this.printTaskTree(queryTask);
    }
    //
    private TaskContext newTaskContext() {
        final HashMap<String, Object> udfData = new HashMap<String, Object>();
        final HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("userID", 12345);
        data.put("status", true);
        udfData.put("name2", "this is name2.");
        udfData.put("age", 31);
        udfData.put("nick", "my name is nick.");
        //
        return new TaskContext() {
            @Override
            public UDF findUDF(String udfName) {
                return new UDF() {
                    @Override
                    public Object call(Map<String, Object> values) {
                        return udfData;
                    }
                };
            }
            @Override
            public Object get(String name) {
                return data.get(name);
            }
        };
    }
    private void printTaskTree(QueryTask queryTask) {
        AbstractQueryTask task = (AbstractQueryTask) queryTask;
        List<AbstractQueryTask> allTask = task.getAllTask();
        int runCount = 0;
        do {
            runCount = 0;
            for (AbstractQueryTask t : allTask) {
                if (t.isWaiting()) {
                    t.run();
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