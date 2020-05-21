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
package net.hasor.dataql.fx.db.likemybatis;
import net.hasor.dataql.fx.db.parser.DefaultFxQuery;
import net.hasor.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 普通SQL节点
 * @author jmxd
 * @version : 2020-05-18
 */
public class TextSqlNode extends SqlNode {
    /** SQL */
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
                Object val = DefaultFxQuery.evalOgnl(expression, paramMap);
                parameters.add(val);
                sql = sql.replaceFirst(expressionRegx.pattern(), "?");
            }
            expressions = extractParameter(replaceRegx, text);
            for (String expression : expressions) {
                Object val = DefaultFxQuery.evalOgnl(expression, paramMap);
                sql = sql.replaceFirst(replaceRegx.pattern(), Objects.toString(val, ""));
            }
        }
        return sql + executeChildren(paramMap, parameters).trim();
    }
}