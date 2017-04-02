package net.test.hasor.graphql;
import net.hasor.graphql.dsl.GraphQuery;
import net.hasor.graphql.task.AbstractQueryTask;
import net.hasor.graphql.task.QueryTask;
import net.hasor.graphql.task.TaskContext;
import net.hasor.graphql.task.TaskParser;
import org.junit.Test;

import java.util.List;
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
        return null;
    }
    private void printTaskTree(QueryTask queryTask) {
        AbstractQueryTask task = (AbstractQueryTask) queryTask;
        List<AbstractQueryTask> allTask = task.getAllTask();
        for (AbstractQueryTask t : allTask) {
            t.run();
        }
        //
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(queryTask.printTaskTree(true));
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(queryTask.printTaskTree(false));
    }
}