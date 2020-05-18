package net.hasor.dataql.fx.db.parser;

import net.hasor.dataql.fx.db.SqlNode;

import java.util.List;
import java.util.Map;

public class MybatisSqlQuery extends DefaultSqlQuery{

    private SqlNode sqlNode;

    public MybatisSqlQuery(SqlNode sqlNode) {
        this.sqlNode = sqlNode;
    }

    @Override
    public String buildQueryString(Object context) {
        if(context instanceof Map){
            return sqlNode.getSql((Map<String, Object>) context);
        }else{
            throw new IllegalArgumentException("context must be instance of Map");
        }
    }

    @Override
    public List<Object> buildParameterSource(Object context) {
        return this.sqlNode.getParameters();
    }
}
