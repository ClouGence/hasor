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
package net.hasor.dataql.fx.db;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.Hints;
import net.hasor.dataql.fx.FxHintNames;
import net.hasor.dataql.fx.FxHintValue;
import net.hasor.dataql.fx.db.parser.*;
import net.hasor.db.jdbc.SqlParameterSource;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.IOUtils;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import javax.inject.Inject;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 支持 SQL 的代码片段执行器。
 *  已支持的语句有：insert、update、delete、replace、select、create、drop、alter
 *  暂不支持语句有：exec、其它语句
 *  已经提供原生：insert、update、delete、replace 语句的批量能力。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-28
 */
public class SqlFragment implements FragmentProcess {
    @Inject
    protected JdbcTemplate jdbcTemplate;

    private static enum SqlMode {
        /** DML：insert、update、delete、replace */
        Execute,
        /** DML：exec */
        Procedure,
        /** DML：select */
        Query,
        /** DDL：create、drop、alter */
        DDL,
        /** Other */
        Unknown,
    }

    public List<Object> batchRunFragment(Hints hint, List<Map<String, Object>> params, String fragmentString) throws Throwable {
        // 如果批量参数为空或者只有一个时，自动退化为非批量
        if (params.size() == 0) {
            return Collections.singletonList(this.runFragment(hint, Collections.emptyMap(), fragmentString));
        }
        if (params.size() == 1) {
            return Collections.singletonList(this.runFragment(hint, params.get(0), fragmentString));
        }
        //
        if (SqlMode.Execute == evalSqlMode(fragmentString)) {
            FxSql fxSql = analysisSQL(fragmentString);
            if (!fxSql.isHavePlaceholder()) {
                fragmentString = fxSql.buildSqlString(params.get(0));
                SqlParameterSource[] parameterArrays = new SqlParameterSource[params.size()];
                for (int i = 0; i < params.size(); i++) {
                    parameterArrays[i] = fxSql.buildParameterSource(params.get(i));
                }
                //
                int[] executeBatch = this.jdbcTemplate.executeBatch(fragmentString, parameterArrays);
                return Arrays.stream(executeBatch).boxed().collect(Collectors.toList());
            }
        }
        return FragmentProcess.super.batchRunFragment(hint, params, fragmentString);
    }

    @Override
    public Object runFragment(Hints hint, Map<String, Object> paramMap, String fragmentString) throws Throwable {
        SqlMode sqlMode = evalSqlMode(fragmentString);
        FxSql fxSql = analysisSQL(fragmentString);
        fragmentString = fxSql.buildSqlString(paramMap);
        SqlParameterSource source = fxSql.buildParameterSource(paramMap);
        //
        if (SqlMode.Query == sqlMode) {
            List<Map<String, Object>> mapList = this.jdbcTemplate.queryForList(fragmentString, source);
            String openPackage = hint.getOrDefault(FxHintNames.FRAGMENT_SQL_OPEN_PACKAGE.name(), FxHintNames.FRAGMENT_SQL_OPEN_PACKAGE.getDefaultVal()).toString();
            //
            // .结果有多条记录,或者模式为 off，那么直接返回List
            boolean packageOff = FxHintValue.FRAGMENT_SQL_OPEN_PACKAGE_OFF.equalsIgnoreCase(openPackage);
            if (packageOff || (mapList != null && mapList.size() > 1)) {
                return mapList;
            }
            // .为空或者结果为空，那么看看是返回 null 或者 空对象
            if (mapList == null || mapList.isEmpty()) {
                if (FxHintValue.FRAGMENT_SQL_OPEN_PACKAGE_COLUMN.equalsIgnoreCase(openPackage)) {
                    return null;
                } else {
                    return Collections.emptyMap();
                }
            }
            // .只有1条记录
            Map<String, Object> rowObject = mapList.get(0);
            if (FxHintValue.FRAGMENT_SQL_OPEN_PACKAGE_COLUMN.equalsIgnoreCase(openPackage)) {
                if (rowObject == null) {
                    return null;
                }
                if (rowObject.size() == 1) {
                    Set<Map.Entry<String, Object>> entrySet = rowObject.entrySet();
                    Map.Entry<String, Object> objectEntry = entrySet.iterator().next();
                    return objectEntry.getValue();
                }
            }
            return rowObject;
            //
        } else if (SqlMode.Execute == sqlMode) {
            return this.jdbcTemplate.executeUpdate(fragmentString, source);
        } else if (SqlMode.Procedure == sqlMode) {
            throw new SQLException("Procedure not support.");
        } else if (SqlMode.DDL == sqlMode) {
            return this.jdbcTemplate.executeUpdate(fragmentString, source);
        }
        throw new SQLException("Unknown SqlMode.");//不可能走到这里
    }

    private static FxSql analysisSQL(String fragmentString) {
        FxSQLLexer lexer = new FxSQLLexer(CharStreams.fromString(fragmentString));
        lexer.removeErrorListeners();
        lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
        //
        FxSQLParser qlParser = new FxSQLParser(new CommonTokenStream(lexer));
        qlParser.removeErrorListeners();
        qlParser.addErrorListener(ThrowingErrorListener.INSTANCE);
        FxSQLParserVisitor visitor = new DefaultFxSQLVisitor();
        return (FxSql) visitor.visit(qlParser.rootInstSet());
    }

    private static SqlMode evalSqlMode(String fragmentString) throws SQLException, IOException {
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
            tempLine = tempLine.toLowerCase();
            if (tempLine.startsWith("insert") || tempLine.startsWith("update") || tempLine.startsWith("delete") || tempLine.startsWith("replace")) {
                sqlMode = SqlMode.Execute;
            } else if (tempLine.startsWith("exec")) {
                sqlMode = SqlMode.Procedure;
            } else if (tempLine.startsWith("select")) {
                sqlMode = SqlMode.Query;
            } else if (tempLine.startsWith("create") || tempLine.startsWith("drop") || tempLine.startsWith("alter")) {
                sqlMode = SqlMode.DDL;
            } else {
                sqlMode = SqlMode.Unknown;
            }
            break;
        }
        if (sqlMode == null) {
            throw new SQLException("Unknown query statement. -> " + fragmentString);
        }
        return sqlMode;
    }
}