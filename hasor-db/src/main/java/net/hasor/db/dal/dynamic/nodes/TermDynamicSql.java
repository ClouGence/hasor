/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.dal.dynamic.nodes;
import net.hasor.db.dal.dynamic.BuilderContext;
import net.hasor.db.dal.dynamic.QuerySqlBuilder;
import net.hasor.utils.ArrayUtils;
import net.hasor.utils.StringUtils;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * 对应XML中 <trim>
 * @author zhangxu
 * @author 赵永春 (zyc@byshell.org)
 * @version : 2021-05-24
 */
public class TermDynamicSql extends ArrayDynamicSql {
    /** 前缀  prefix*/
    private final String   prefix;
    /** 后缀  suffix*/
    private final String   suffix;
    /** 前缀 prefixOverrides */
    private final String[] prefixOverrides;
    /** 后缀 suffixOverrides */
    private final String[] suffixOverrides;
    /** 匹配模式是否大小写敏感 */
    private final boolean  caseSensitive;

    public TermDynamicSql(String prefix, String suffix, String prefixOverrides, String suffixOverrides, boolean caseSensitive) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.caseSensitive = caseSensitive;
        this.prefixOverrides = StringUtils.isBlank(prefixOverrides) ? ArrayUtils.EMPTY_STRING_ARRAY : //
                Arrays.stream(prefixOverrides.split("\\|")).map(String::trim).toArray(String[]::new);
        this.suffixOverrides = StringUtils.isBlank(suffixOverrides) ? ArrayUtils.EMPTY_STRING_ARRAY : //
                Arrays.stream(suffixOverrides.split("\\|")).map(String::trim).toArray(String[]::new);
    }

    private static boolean startsWith(boolean caseSensitive, String test, String prefix) {
        if (caseSensitive) {
            return StringUtils.startsWithIgnoreCase(test.trim(), prefix);
        } else {
            return test.startsWith(prefix);
        }
    }

    private static boolean endsWith(boolean caseSensitive, String test, String suffix) {
        if (caseSensitive) {
            return StringUtils.endsWithIgnoreCase(test.trim(), suffix);
        } else {
            return test.trim().endsWith(suffix);
        }
    }

    @Override
    public void buildQuery(BuilderContext builderContext, QuerySqlBuilder querySqlBuilder) throws SQLException {
        QuerySqlBuilder tempQuerySqlBuilder = new QuerySqlBuilder();
        super.buildQuery(builderContext, tempQuerySqlBuilder);
        //
        String childrenSql = tempQuerySqlBuilder.getSqlString().trim();
        if (StringUtils.isNotBlank(childrenSql)) {
            querySqlBuilder.appendSql(StringUtils.defaultString(this.prefix) + " "); // 开始拼接SQL
            //
            // 去掉prefixOverrides
            for (String override : this.prefixOverrides) {
                override = override.trim();
                if (StringUtils.isBlank(override)) {
                    continue;
                }
                if (startsWith(this.caseSensitive, childrenSql, override)) {
                    childrenSql = childrenSql.substring(childrenSql.indexOf(override) + override.length());
                    break;
                }
            }
            // 去掉 suffixOverrides
            for (String override : this.suffixOverrides) {
                if (endsWith(this.caseSensitive, childrenSql, override)) {
                    childrenSql = childrenSql.substring(0, childrenSql.lastIndexOf(override));
                    break;
                }
            }
            querySqlBuilder.appendSql(childrenSql);
            querySqlBuilder.appendSql(" " + StringUtils.defaultString(this.suffix)); // 拼接结束SQL
        }
        //
        querySqlBuilder.appendArgs(tempQuerySqlBuilder.originalArgList());
    }
}