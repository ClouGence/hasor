package net.test.hasor.dataql;
import com.alibaba.fastjson.JSON;
import net.hasor.core.Settings;
import net.hasor.dataql.Query;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.domain.compiler.QIL;
import net.hasor.dataql.domain.compiler.QueryCompiler;
import net.hasor.dataql.domain.parser.ParseException;
import net.hasor.dataql.runtime.QueryRuntime;
import net.hasor.dataql.udfs.collection.First;
import net.hasor.dataql.udfs.collection.Foreach;
import net.hasor.dataql.udfs.collection.Last;
import net.hasor.dataql.udfs.collection.Limit;
import net.hasor.utils.IOUtils;
import net.hasor.utils.ResourcesUtils;
import net.test.hasor.dataql.udfs.*;
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
        //
        this.runtime.addShareUDF("findUserByID", new FindUserByID());
        this.runtime.addShareUDF("queryOrder", new QueryOrder());
        this.runtime.addShareUDF("userManager.findUserByID", new UserManager());
        this.runtime.addShareUDF("foo", new Foo());
        this.runtime.addShareUDF("double", new DoubleNumber());
    }
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
        // - 执行 QL
        try {
            //
            QIL compilerQuery = QueryCompiler.compilerQuery(buildQuery);
            Query query = this.runtime.createEngine(compilerQuery).newQuery();
            query.addParameter("uid", "uid form env");
            query.addParameter("sid", "sid form env");
            //
            QueryResult result = null;
            int queryCount = 10000;
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
            System.out.println((sumTime / queryCount) + "/" + maxTime + " - " + JSON.toJSON(result).toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    //
    //
    // --------------------------------------------------------------------------------------------
    @Test
    public void mainALL() throws Exception {
        this.printTaskTree("/dataql/dataql_" + 3 + ".ql");
    }
}