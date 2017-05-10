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
import com.alibaba.fastjson.JSON;
import net.hasor.data.ql.Query;
import net.hasor.data.ql.QueryResult;
import net.hasor.data.ql.ctx.GraphContext;
import net.hasor.data.ql.dsl.QueryModel;
import net.hasor.data.ql.runtime.QueryTask;
import net.hasor.data.ql.runtime.TaskParser;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
/**
 * @version : 2014-7-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class CallTaskTest extends AbstractTaskTest {
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
    private void printTaskTree(QueryModel queryModel) {
        String buildQuery = queryModel.buildQuery();
        // - 执行计划
        {
            QueryTask queryTask = new TaskParser().doParser(queryModel.getDomain());
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println(queryTask.printStrutsTree());
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println(queryTask.printTaskTree());
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        // - 执行 QL
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uid", "uid form env");
            params.put("sid", "sid form env");
            //
            GraphContext gc = appContext.getInstance(GraphContext.class);
            Query query = gc.createQuery(buildQuery);
            QueryResult result = query.doQuery(params);
            System.out.println(JSON.toJSON(result).toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}