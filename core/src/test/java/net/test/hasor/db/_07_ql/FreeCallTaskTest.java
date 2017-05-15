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
import net.hasor.core.Settings;
import net.hasor.core.utils.IOUtils;
import net.hasor.core.utils.ResourcesUtils;
import net.hasor.data.ql.Query;
import net.hasor.data.ql.QueryResult;
import net.hasor.data.ql.ctx.GraphContext;
import net.hasor.data.ql.dsl.QueryModel;
import net.hasor.data.ql.dsl.parser.DataQLParser;
import net.hasor.data.ql.dsl.parser.ParseException;
import net.hasor.data.ql.runtime.QueryTask;
import net.hasor.data.ql.runtime.TaskParser;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
/**
 * @version : 2014-7-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class FreeCallTaskTest extends AbstractTaskTest {
    @Test
    public void main1() throws Exception {
        this.printTaskTree("/graphql/graphql_1.ql");
    }
    @Test
    public void main2() throws Exception {
        this.printTaskTree("/graphql/graphql_2.ql");
    }
    @Test
    public void main3() throws Exception {
        this.printTaskTree("/graphql/graphql_3.ql");
    }
    @Test
    public void main4() throws Exception {
        this.printTaskTree("/graphql/graphql_4.ql");
    }
    @Test
    public void main5() throws Exception {
        this.printTaskTree("/graphql/graphql_5.ql");
    }
    @Test
    public void main6() throws Exception {
        this.printTaskTree("/graphql/graphql_6.ql");
    }
    @Test
    public void main7() throws Exception {
        this.printTaskTree("/graphql/graphql_7.ql");
    }
    @Test
    public void main8() throws Exception {
        this.printTaskTree("/graphql/graphql_8.ql");
    }
    @Test
    public void main9() throws Exception {
        this.printTaskTree("/graphql/graphql_9.ql");
    }
    @Test
    public void mainALL() throws Exception {
        for (int i = 0; i < 100; i++) {
            this.printTaskTree("/graphql/graphql_" + i + ".ql");
        }
    }
    //
    // --------------------------------------------------------------------------------------------
    private void printTaskTree(String queryResource) throws IOException, ParseException {
        InputStream inStream = ResourcesUtils.getResourceAsStream(queryResource);
        if (inStream == null) {
            return;
        }
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("resource = " + queryResource);
        InputStreamReader reader = new InputStreamReader(inStream, Charset.forName(Settings.DefaultCharset));
        StringWriter outWriter = new StringWriter();
        IOUtils.copy(reader, outWriter);
        String buildQuery = outWriter.toString();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(buildQuery);
        //
        // - 执行计划
        {
            QueryModel graphPlan = DataQLParser.parserQL(buildQuery);
            QueryTask queryTask = new TaskParser().doParser(graphPlan.getDomain());
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