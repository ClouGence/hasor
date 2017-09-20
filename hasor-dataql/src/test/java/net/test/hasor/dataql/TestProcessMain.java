package net.test.hasor.dataql;
import com.alibaba.fastjson.JSON;
import net.hasor.core.Settings;
import net.hasor.dataql.Query;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.domain.compiler.QIL;
import net.hasor.dataql.domain.compiler.QueryCompiler;
import net.hasor.dataql.domain.parser.ParseException;
import net.hasor.dataql.result.LambdaModel;
import net.hasor.dataql.runtime.QueryRuntime;
import net.hasor.dataql.udfs.collection.*;
import net.hasor.utils.IOUtils;
import net.hasor.utils.ResourcesUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
/**
 * Created by yongchun.zyc on 2017/7/17.
 */
public class TestProcessMain {
    private QueryRuntime runtime = new QueryRuntime();
    @Before
    public void before() {
        this.runtime.addShareUDF("foreach", new Foreach());
        this.runtime.addShareUDF("first", new First());
        this.runtime.addShareUDF("last", new Last());
        this.runtime.addShareUDF("limit", new Limit());
        this.runtime.addShareUDF("addTo", new AddTo());
        //
        this.runtime.addShareUDF("findUserByID", new UdfManager.FindUserByID());
        this.runtime.addShareUDF("queryOrder", new UdfManager.QueryOrder());
        this.runtime.addShareUDF("userManager.findUserByID", new UdfManager.UserInfo());
        this.runtime.addShareUDF("foo", new UdfManager.Foo());
        this.runtime.addShareUDF("double", new UdfManager.DoubleNumber());
        this.runtime.addShareUDF("filter", new UdfManager.Filter());
    }
    private void printTaskTree(String queryResource) throws IOException, ParseException {
        InputStream inStream = ResourcesUtils.getResourceAsStream(queryResource);
        if (inStream == null) {
            return;
        }
        //
        // .获取 DataQL 查询字符串
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("resource = " + queryResource);
        InputStreamReader reader = new InputStreamReader(inStream, Charset.forName(Settings.DefaultCharset));
        StringWriter outWriter = new StringWriter();
        IOUtils.copy(reader, outWriter);
        String buildQuery = outWriter.toString();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(buildQuery);
        //
        // .编译 DataQL 为 QIL
        QIL compilerQuery = QueryCompiler.compilerQuery(buildQuery);
        // .通过 QIL 构建 Query
        Query query = this.runtime.createEngine(compilerQuery).newQuery();
        query.addParameter("uid", "uid form env");
        query.addParameter("sid", "sid form env");
        //
        // .执行查询
        try {
            QueryResult result = null;
            int queryCount = 1;
            long sumTime = 0;
            long maxTime = 0;
            for (int i = 0; i < queryCount; i++) {
                result = query.execute();
                long executionTime = result.executionTime();
                sumTime += executionTime;
                if (maxTime < executionTime) {
                    maxTime = executionTime;
                }
            }
            //
            Object data = result.getData();
            if (data instanceof LambdaModel) {
                data = ((LambdaModel) data).call(new Object[] { -1 }, null);
            }
            //
            System.out.println((sumTime / queryCount) + "/" + maxTime + " - " + JSON.toJSON(data).toString());
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    //
    //
    // --------------------------------------------------------------------------------------------
    @Test
    public void mainALL() throws Exception {
        System.out.println(16 & 19);
        for (int i = 0; i <= 12; i++)
            this.printTaskTree("/dataql/dataql_" + i + ".ql");
        //        this.printTaskTree("/test/test_" + 1 + ".ql");
    }
}