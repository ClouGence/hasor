package net.hasor.dataql.fx.db;


import net.hasor.dataql.fx.db.parser.DefaultSqlQuery;
import net.hasor.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 普通SQL节点
 */
public class TextSqlNode extends SqlNode {

    /**
     * SQL
     */
    private String text;

    public TextSqlNode(String text) {
        this.text = text;
    }

    @Override
    public String getSql(Map<String, Object> paramMap, List<Object> parameters) {
        String sql = text;
        if (StringUtils.isNotBlank(text)) {
            // 提取#{}表达式
            List<String> expressions = extractParameter(expressionRegx, text);
            for (String expression : expressions) {
                // 执行表达式
                Object val = DefaultSqlQuery.evalOgnl(expression, paramMap);
                parameters.add(val);
                sql = sql.replaceFirst(expressionRegx.pattern(), "?");
            }
            expressions = extractParameter(replaceRegx, text);
            for (String expression : expressions) {
                Object val = DefaultSqlQuery.evalOgnl(expression, paramMap);
                sql = sql.replaceFirst(replaceRegx.pattern(), Objects.toString(val, ""));
            }
        }
        return sql + executeChildren(paramMap, parameters).trim();
    }

}
