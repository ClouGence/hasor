/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.test.hasor.db._07_ql;
import net.hasor.data.ql.dsl.QueryModel;
import net.hasor.data.ql.runtime.QueryTask;
import net.hasor.data.ql.runtime.TaskParser;
import org.junit.Test;
/**
 * @version : 2014-7-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class TaskTreeTest {
    @Test
    public void main1() {
        QueryModel queryModel = new GraphQLTest().main1();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main2() {
        QueryModel queryModel = new GraphQLTest().main2();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main3() {
        QueryModel queryModel = new GraphQLTest().main3();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main4() {
        QueryModel queryModel = new GraphQLTest().main4();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main5() {
        QueryModel queryModel = new GraphQLTest().main5();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main6() {
        QueryModel queryModel = new GraphQLTest().main6();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main7() {
        QueryModel queryModel = new GraphQLTest().main7();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main8() {
        QueryModel queryModel = new GraphQLTest().main8();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    @Test
    public void main9() {
        QueryModel queryModel = new GraphQLTest().main9();
        QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
        this.printTaskTree(queryTask);
    }
    //
    private void printTaskTree(QueryTask queryTask) {
        //
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(queryTask.printStrutsTree());
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(queryTask.printTaskTree());
        //
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
}