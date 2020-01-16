package net.example.hasor.commands;
import com.alibaba.fastjson.JSON;
import net.hasor.core.Inject;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.Query;
import net.hasor.dataql.domain.DataModel;
import net.hasor.tconsole.TelCommand;
import net.hasor.tconsole.TelExecutor;

public abstract class AbstractTelExecutor implements TelExecutor {
    @Inject
    protected DataQL dataQL;

    protected String doQuery(TelCommand telCommand, Query qlQuery) {
        DataModel dataModel = qlQuery.execute(telCommand.getCommandArgs()).getData();
        if (dataModel.isValueModel()) {
            return String.valueOf(dataModel.unwrap());
        } else {
            return JSON.toJSONString(dataModel.unwrap(), true);
        }
    }
}
