package net.hasor.dataql.fx.db;

import net.hasor.dataql.fx.db.parser.DefaultSqlQuery;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 对应XML中 <if>
 */
public class IfSqlNode extends SqlNode {

    /**
     * 判断表达式
     */
    private String test;

    public IfSqlNode(String test) {
        this.test = test;
    }

    @Override
    public String getSql(Map<String, Object> paramMap, List<Object> parameters) {
        // 执行表达式
        Object value = DefaultSqlQuery.evalOgnl(test, paramMap);
        // 判断表达式返回结果是否是true，如果不是则过滤子节点
        if (Objects.equals(value, true)) {
            return executeChildren(paramMap, parameters);
        }
        return "";
    }
}
