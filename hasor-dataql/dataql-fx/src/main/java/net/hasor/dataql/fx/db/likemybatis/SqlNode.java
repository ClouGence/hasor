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
import net.hasor.dataql.fx.db.runsql.SqlFragment;
import net.hasor.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql节点
 * @author jmxd
 * @version : 2020-05-18
 */
public abstract class SqlNode {
    /** 提取#{}的正则 */
    final Pattern expressionRegx = Pattern.compile("#\\{(.*?)\\}");
    /** 提取${}的正则 */
    final Pattern replaceRegx    = Pattern.compile("\\$\\{(.*?)\\}");
    /** 子节点 */
    List<SqlNode>       nodes = new ArrayList<>();
    /** SQL参数 */
    List<Object>        parameters;
    SqlFragment.SqlMode sqlMode;

    /** 追加子节点 */
    public void addChildNode(SqlNode node) {
        this.nodes.add(node);
    }

    /** 获取该节点的SQL */
    public String getSql(Map<String, Object> paramMap) {
        this.parameters = new ArrayList<>();
        return getSql(paramMap, parameters);
    }

    /** 获取该节点的SQL */
    public abstract String getSql(Map<String, Object> paramMap, List<Object> parameters);

    /** 获取子节点SQL */
    public String executeChildren(Map<String, Object> paramMap, List<Object> parameters) {
        String sql = "";
        for (SqlNode node : nodes) {
            sql += StringUtils.defaultString(node.getSql(paramMap, parameters)) + " ";
        }
        return sql;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    /**
     * 根据正则表达式提取参数
     * @param pattern 正则表达式
     * @param sql     SQL
     */
    public List<String> extractParameter(Pattern pattern, String sql) {
        Matcher matcher = pattern.matcher(sql);
        List<String> results = new ArrayList<>();
        while (matcher.find()) {
            results.add(matcher.group(1));
        }
        return results;
    }

    public SqlFragment.SqlMode getSqlMode() {
        return sqlMode;
    }

    public void setSqlNode(SqlFragment.SqlMode sqlMode) {
        this.sqlMode = sqlMode;
    }
}