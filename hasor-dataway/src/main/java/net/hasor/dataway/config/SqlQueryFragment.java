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
package net.hasor.dataway.config;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.Hints;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.IOUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Dataway 内嵌版的数据库执行器
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@Singleton
public class SqlQueryFragment implements FragmentProcess {
    @Inject
    private JdbcTemplate jdbcTemplate;

    private static enum SqlMode {
        Execute, Procedure, Query
    }

    @Override
    public Object runFragment(Hints hint, Map<String, Object> paramMap, String fragmentString) throws Throwable {
        List<String> readLines = IOUtils.readLines(new StringReader(fragmentString));
        SqlMode sqlMode = null;
        boolean multipleLines = false;
        for (String lineStr : readLines) {
            String tempLine = lineStr.trim();
            if (!multipleLines) {
                // 空行
                if (StringUtils.isBlank(tempLine)) {
                    continue;
                }
                // 单行注释
                if (tempLine.startsWith("--") && tempLine.startsWith("#")) {
                    continue;
                }
                // 多行注释
                if (tempLine.startsWith("/*")) {
                    multipleLines = true;
                }
            }
            if (multipleLines) {
                if (tempLine.contains("*/")) {
                    tempLine = tempLine.substring(tempLine.indexOf("*/")).trim();
                    multipleLines = false;
                } else {
                    continue;
                }
            }
            //
            if (tempLine.startsWith("insert") || tempLine.startsWith("update") || tempLine.startsWith("delete")) {
                sqlMode = SqlMode.Execute;
            } else if (tempLine.startsWith("exec")) {
                sqlMode = SqlMode.Procedure;
            } else {
                sqlMode = SqlMode.Query;
            }
            break;
        }
        if (sqlMode == null) {
            throw new SQLException("Unknown query statement. -> " + fragmentString);
        }
        //
        if (SqlMode.Query == sqlMode) {
            List<Map<String, Object>> mapList = this.jdbcTemplate.queryForList(fragmentString, paramMap);
            if (mapList != null && mapList.size() == 1) {
                Map<String, Object> objectMap = mapList.get(0);
                if (objectMap != null && objectMap.size() == 1) {
                    Set<Map.Entry<String, Object>> entrySet = objectMap.entrySet();
                    Map.Entry<String, Object> objectEntry = entrySet.iterator().next();
                    return objectEntry.getValue();
                } else {
                    return objectMap;
                }
            }
            return mapList;
        } else if (SqlMode.Execute == sqlMode) {
            return this.jdbcTemplate.executeUpdate(fragmentString, paramMap);
        } else if (SqlMode.Procedure == sqlMode) {
            throw new SQLException("Procedure not support.");
        }
        throw new SQLException("Unknown SqlMode.");//不可能走到这里
    }
}
