package net.example.hasor.execs;
import com.alibaba.fastjson.JSON;
import net.hasor.core.Inject;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.domain.DataModel;
import net.hasor.tconsole.TelExecutor;

public abstract class AbstractTelExecutor implements TelExecutor {
    @Inject
    protected DataQL dataQL;

    protected String doQuery(QueryResult queryResult) {
        DataModel dataModel = queryResult.getData();
        if (dataModel.isValue()) {
            return String.valueOf(dataModel.unwrap());
        } else {
            return JSON.toJSONString(dataModel.unwrap(), true);
        }
    }
}
