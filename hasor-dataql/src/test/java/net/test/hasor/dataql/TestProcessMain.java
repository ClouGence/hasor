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
package net.test.hasor.dataql;
import com.alibaba.fastjson.JSON;
import net.hasor.core.*;
import net.hasor.dataql.Query;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.UdfManager;
import net.hasor.dataql.UdfSource;
import net.hasor.dataql.binder.DataApiBinder;
import net.hasor.dataql.binder.DataQL;
import net.hasor.dataql.domain.compiler.QIL;
import net.hasor.dataql.domain.compiler.QueryCompiler;
import net.hasor.dataql.result.LambdaModel;
import net.hasor.dataql.runtime.QueryEngine;
import net.hasor.dataql.udf.SimpleUdfManager;
import net.hasor.dataql.udf.SimpleUdfSource;
import net.hasor.dataql.udf.funs.CollectionUDFs;
import net.hasor.dataql.udf.source.TypeUdfSource;
import net.hasor.utils.IOUtils;
import net.hasor.utils.ResourcesUtils;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
/**
 * 测试用例
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
public class TestProcessMain {
    private static final String[] testQl;

    static {
        testQl = new String[] {//
                "/basic/dataql_1.ql",//
                "/basic/dataql_2.ql",//
                "/basic/dataql_3.ql",//
                "/basic/dataql_4.ql",//
                "/basic/dataql_5.ql",//
                "/basic/dataql_6.ql",//
                "/basic/dataql_7.ql",//
                "/basic/dataql_8.ql",//
                //
                "/eval/dataql_10.ql",//
                "/eval/dataql_11.ql",//
                "/eval/dataql_12.ql",//
                "/eval/dataql_13.ql",//
                //
                "/lambda/dataql_20.ql",//
                "/lambda/dataql_21.ql",//
                "/lambda/dataql_22.ql",//
                "/lambda/dataql_23.ql",//
                "/lambda/dataql_24.ql",//
                "/lambda/dataql_25.ql"//
        };
    }

    private static UdfSource udfSource = new SimpleUdfSource();
    @Before
    public void before() {
        udfSource.addUdf("findUserByID", new FooManager.FindUserByID());
        udfSource.addUdf("queryOrder", new FooManager.QueryOrder());
        udfSource.addUdf("userManager.findUserByID", new FooManager.UserInfo());
        udfSource.addUdf("foo", new FooManager.Foo());
        udfSource.addUdf("double", new FooManager.DoubleNumber());
        udfSource.addUdf("filter", new FooManager.Filter());
        udfSource.addUdf("track", new FooManager.Track());
    }
    //
    private String getScript(String queryResource) throws IOException {
        InputStream inStream = ResourcesUtils.getResourceAsStream(queryResource);
        if (inStream == null) {
            return "";
        }
        // .获取 DataQL 查询字符串
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("resource = " + queryResource);
        InputStreamReader reader = new InputStreamReader(inStream, Charset.forName(Settings.DefaultCharset));
        StringWriter outWriter = new StringWriter();
        IOUtils.copy(reader, outWriter);
        String buildQuery = outWriter.toString();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(buildQuery);
        return buildQuery;
    }
    private void printResult(QueryResult result) throws Throwable {
        Object data = result.getData();
        if (data instanceof LambdaModel) {
            data = "外部调用结果：" + ((LambdaModel) data).call(new Object[] { -1 }, null);
        }
        System.out.println(" - " + JSON.toJSON(data).toString());
    }
    //
    // --------------------------------------------------------------------------------------------
    @Test
    public void mainALL() throws Throwable {
        //        for (int i = 0; i < testQl.length; i++) {
        //            this.printTaskTree(testQl[i]);
        //        }
        String buildQuery = getScript("/basic/dataql_1.ql");
        QueryResult result = null;
        //
        // --------------------------------------------------------------------------------------------------
        // .JSR223方式
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("dataql");
        ((UdfManager) scriptEngine).addDefaultSource(udfSource);
        ((UdfManager) scriptEngine).addDefaultSource(new TypeUdfSource<CollectionUDFs>(CollectionUDFs.class, null, null));
        SimpleScriptContext params = new SimpleScriptContext();
        params.setAttribute("uid", "uid form env", ScriptContext.ENGINE_SCOPE);
        params.setAttribute("sid", "sid form env", ScriptContext.ENGINE_SCOPE);
        result = (QueryResult) scriptEngine.eval(buildQuery);
        printResult(result);
        //
        // --------------------------------------------------------------------------------------------------
        // .DataQL原生方式
        UdfManager udfManager = new SimpleUdfManager();
        udfManager.addDefaultSource(udfSource);
        udfManager.addDefaultSource(new TypeUdfSource<CollectionUDFs>(CollectionUDFs.class, null, null));
        QIL compilerQuery = QueryCompiler.compilerQuery(buildQuery);        //编译 DataQL 为 QIL
        Query query = new QueryEngine(udfManager, compilerQuery).newQuery();//通过 QIL 构建 Query
        query.addParameter("uid", "uid form env");
        query.addParameter("sid", "sid form env");
        result = query.execute();
        printResult(result);
        //
        // --------------------------------------------------------------------------------------------------
        // .Hasor框架中使用DataQL
        AppContext appContext = Hasor.createAppContext(new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                apiBinder.tryCast(DataApiBinder.class).addDefaultUdfSource(udfSource);
            }
        });
        DataQL dataQL = appContext.getInstance(DataQL.class);
        Query qlQuery = dataQL.createQuery(buildQuery);
        qlQuery.addParameter("uid", "uid form env");
        qlQuery.addParameter("sid", "sid form env");
        result = qlQuery.execute();
        printResult(result);
    }
    //
    @Test
    public void performanceALL() throws Throwable {
        String buildQuery = getScript("/basic/dataql_1.ql");
        UdfManager udfManager = new SimpleUdfManager();
        udfManager.addDefaultSource(udfSource);
        QIL compilerQuery = QueryCompiler.compilerQuery(buildQuery);        //编译 DataQL 为 QIL
        Query query = new QueryEngine(udfManager, compilerQuery).newQuery();//通过 QIL 构建 Query
        query.addParameter("uid", "uid form env");
        query.addParameter("sid", "sid form env");
        //
        // .执行查询
        try {
            Object data = null;
            int queryCount = 10000;
            long sumTime = 0;
            long maxTime = 0;
            for (int i = 0; i < queryCount; i++) {
                QueryResult result = query.execute();
                long executionTime = result.executionTime();
                sumTime += executionTime;
                if (maxTime < executionTime) {
                    maxTime = executionTime;
                }
                //
                data = result.getData();
                if (data instanceof LambdaModel) {
                    data = "外部调用结果：" + ((LambdaModel) data).call(new Object[] { -1 }, null);
                }
            }
            //
            System.out.println((sumTime / queryCount) + "/" + maxTime + " - " + JSON.toJSON(data).toString());
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}