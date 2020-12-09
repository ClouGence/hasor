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
package net.hasor.dataql.fx.db.runsql;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.Hints;
import net.hasor.dataql.fx.FxHintNames;
import net.hasor.dataql.fx.FxHintValue;
import net.hasor.dataql.fx.basic.StringUdfSource;
import net.hasor.dataql.fx.db.FxSqlCheckChainSpi;
import net.hasor.dataql.fx.db.FxSqlCheckChainSpi.FxSqlInfo;
import net.hasor.dataql.fx.db.LookupConnectionListener;
import net.hasor.dataql.fx.db.LookupDataSourceListener;
import net.hasor.dataql.fx.db.fxquery.DefaultFxQuery;
import net.hasor.dataql.fx.db.fxquery.FxQuery;
import net.hasor.dataql.fx.db.runsql.dialect.SqlPageDialect;
import net.hasor.db.JdbcUtils;
import net.hasor.db.jdbc.*;
import net.hasor.db.jdbc.core.ArgPreparedStatementSetter;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.core.RowMapperResultSetExtractor;
import net.hasor.db.jdbc.mapper.ColumnMapRowMapper;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.IOUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static net.hasor.dataql.fx.FxHintNames.*;
import static net.hasor.dataql.fx.FxHintValue.*;

/**
 * 支持 SQL 的代码片段执行器。整合了分页、批处理能力。
 *  已支持的语句有：insert、update、delete、replace、select、create、drop、alter
 *  已经提供原生：insert、update、delete、replace 语句的批量能力。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-28
 */
@Singleton
public class SqlFragment implements FragmentProcess {
    @Inject
    protected AppContext              appContext;
    @Inject
    protected SpiTrigger              spiTrigger;
    private   DataSource              defaultDataSource;
    private   Map<String, DataSource> dataSourceMap;

    /** SqlMode 目前只会在 '批量' 和 '分页' 两个场景下才参与判断。 */
    public static enum SqlMode {
        /** DML：insert、update、delete、replace、exec */
        Insert, Update, Delete, Procedure,
        /** DML：select */
        Query,
        /** DDL：create、drop、alter */
        Create, Drop, Alter,
        /** Other */
        Other,
    }

    private static final ThreadLocal<ResultSetExtractor<List<Map<String, Object>>>> RESULT_EXTRACTOR = ThreadLocal.withInitial(() -> {
        return new RowMapperResultSetExtractor<>(new ColumnMapRowMapper());
    });

    @PostConstruct
    public void init() {
        this.dataSourceMap = new HashMap<>();
        List<BindInfo<DataSource>> bindInfos = this.appContext.findBindingRegister(DataSource.class);
        for (BindInfo<DataSource> bindInfo : bindInfos) {
            if (StringUtils.isBlank(bindInfo.getBindName())) {
                if (this.defaultDataSource == null) {
                    this.defaultDataSource = this.appContext.getInstance(bindInfo);
                }
            } else {
                DataSource dataSource = this.appContext.getInstance(bindInfo);
                if (dataSource != null) {
                    this.dataSourceMap.put(bindInfo.getBindName(), dataSource);
                }
            }
        }
    }

    protected JdbcTemplate getJdbcTemplate(String sourceName) {
        // .首先尝试 Connection
        if (this.spiTrigger.hasSpi(LookupConnectionListener.class)) {
            // .通过 SPI 查找数据源
            Connection jdbcConnection = this.spiTrigger.notifySpi(LookupConnectionListener.class, (listener, lastResult) -> {
                return listener.lookUp(sourceName);
            }, null);
            // .构造JdbcTemplate
            if (jdbcConnection != null) {
                return new JdbcTemplate(jdbcConnection);
            }
        }
        // .其次在通过数据源获取
        DataSource useDataSource = null;
        if (StringUtils.isBlank(sourceName)) {
            useDataSource = this.defaultDataSource;
        } else {
            useDataSource = this.dataSourceMap.get(sourceName);
        }
        if (useDataSource == null) {
            if (this.spiTrigger.hasSpi(LookupDataSourceListener.class)) {
                // .通过 SPI 查找数据源
                DataSource dataSource = this.spiTrigger.notifySpi(LookupDataSourceListener.class, (listener, lastResult) -> {
                    return listener.lookUp(sourceName);
                }, null);
                // .构造JdbcTemplate
                if (dataSource != null) {
                    return new JdbcTemplate(dataSource);
                }
            }
            throw new NullPointerException("DataSource " + sourceName + " is undefined.");
        }
        return new JdbcTemplate(useDataSource);
    }

    public List<Object> batchRunFragment(Hints hint, List<Map<String, Object>> params, String fragmentString) throws Throwable {
        if (params == null || params.size() == 0) {
            // 如果批量参数为空退：退化为 非批量
            return Collections.singletonList(this.runFragment(hint, Collections.emptyMap(), fragmentString));
        }
        if (params.size() == 1) {
            // 批量参数只有一组：退化为 非批量
            return Collections.singletonList(this.runFragment(hint, params.get(0), fragmentString));
        }
        FxQuery fxSql = analysisSQL(hint, fragmentString);
        String tempFragmentString = fxSql.buildQueryString(params.get(0));
        boolean useBatch = true;
        if (fxSql.isHavePlaceholder()) {
            // 分析SQL后如果含有占位符：退化为 非批量（占位符会导致每次执行的SQL语句可能不一样）
            useBatch = false;
        } else {
            // Insert/Update/Delete 语句之外的：退化为 非批量
            SqlMode sqlMode = evalSqlMode(tempFragmentString);
            useBatch = (SqlMode.Insert == sqlMode || SqlMode.Update == sqlMode || SqlMode.Delete == sqlMode);
        }
        //
        // --- 非批量模式
        if (!useBatch) {
            List<Object> resultList = new ArrayList<>(params.size());
            for (Map<String, Object> paramItem : params) {
                if (usePage(hint)) {
                    resultList.add(this.usePageFragment(fxSql, hint, paramItem));
                } else {
                    resultList.add(this.noPageFragment(fxSql, hint, paramItem));
                }
            }
            return resultList;
        }
        //
        // --- 批量模式
        List<Object[]> batchParam = params.stream().map(preparedParams -> {
            return fxSql.buildParameterSource(preparedParams).toArray();
        }).collect(Collectors.toList());
        String sourceName = hint.getOrDefault(FRAGMENT_SQL_DATA_SOURCE.name(), "").toString();
        //
        return this.executeSQL(true, sourceName, tempFragmentString, batchParam.toArray(), new SqlQuery<List<Object>>() {
            public List<Object> doQuery(String querySQL, Object[] params, JdbcTemplate useJdbcTemplate) throws SQLException {
                PreparedStatementSetter[] parameterArrays = Arrays.stream(params).map(o -> {
                    return new ArgPreparedStatementSetter((Object[]) o);
                }).toArray(PreparedStatementSetter[]::new);
                int[] executeBatch = useJdbcTemplate.executeBatch(querySQL, new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        parameterArrays[i].setValues(ps);
                    }

                    public int getBatchSize() {
                        return parameterArrays.length;
                    }
                });
                return Arrays.stream(executeBatch).boxed().collect(Collectors.toList());
            }
        });
    }

    @Override
    public Object runFragment(Hints hint, Map<String, Object> paramMap, String fragmentString) throws Throwable {
        FxQuery fxSql = analysisSQL(hint, fragmentString);
        if (usePage(hint) && evalSqlMode(fragmentString) == SqlMode.Query) {
            return this.usePageFragment(fxSql, hint, paramMap);
        } else {
            return this.noPageFragment(fxSql, hint, paramMap);
        }
    }

    /** 分页模式 */
    protected Object usePageFragment(FxQuery fxSql, Hints hints, Map<String, Object> paramMap) throws SQLException {
        // .优先从 hint 中取方言，取不到在自动推断
        String sqlDialect = hints.getOrDefault(FRAGMENT_SQL_PAGE_DIALECT.name(), "").toString();
        if (StringUtils.isBlank(sqlDialect)) {
            String useDataSource = hints.getOrDefault(FRAGMENT_SQL_DATA_SOURCE.name(), "").toString();
            sqlDialect = getJdbcTemplate(useDataSource).execute((ConnectionCallback<String>) con -> {
                String jdbcUrl = con.getMetaData().getURL();
                String jdbcDriverName = con.getMetaData().getDriverName();
                return JdbcUtils.getDbType(jdbcUrl, jdbcDriverName);
            });
            if (StringUtils.isBlank(sqlDialect)) {
                throw new IllegalArgumentException("Query dialect missing.");
            }
        }
        //
        final SqlPageDialect pageDialect = SqlPageDialectRegister.findOrCreate(sqlDialect, this.appContext);
        //        Hints hints,                        // 查询包含的 Hint
        //        FxQuery fxQuery,                    // 查询语句
        //        Map<String, Object> queryParamMap,  // 查询参数
        //        SqlPageDialect pageDialect,         // 分页方言服务
        //        SqlPageQuery pageQuery              // 用于执行分页查询的服务
        return new SqlPageObject(hints, fxSql, paramMap, pageDialect, this);
    }

    /** 非分页模式 */
    protected Object noPageFragment(FxQuery fxSql, Hints hint, Map<String, Object> paramMap) throws Throwable {
        // 获取必要的参数
        String useSourceName = hint.getOrDefault(FRAGMENT_SQL_DATA_SOURCE.name(), "").toString();
        String buildQueryString = fxSql.buildQueryString(paramMap);
        Object[] buildQueryParams = fxSql.buildParameterSource(paramMap).toArray();
        // 使用 preparedCallback 执行查询
        return this.executeSQL(useSourceName, buildQueryString, buildQueryParams, (queryString, queryParams, useJdbcTemplate) -> {
            // 准备 preparedCallback
            PreparedStatementCallback<Object> preparedCallback = ps -> {
                // 接收返回结果使用
                List<Object> resultDataSet = new ArrayList<>();
                String keepType = hint.getOrDefault(FRAGMENT_SQL_MULTIPLE_QUERIES.name(), FRAGMENT_SQL_MULTIPLE_QUERIES_LAST).toString();
                // 设置请求参数
                new ArgPreparedStatementSetter(queryParams).setValues(ps);
                //
                // 执行多Sql执行，并处理第一个结果
                if (ps.execute()) {
                    // -- 第一个结果是个结果集
                    ResultSet resultSet = ps.getResultSet();
                    // -- 结果
                    resultDataSet.add(dataExtractor(hint, resultSet));
                } else {
                    // -- 第一个结果是个影响行数
                    resultDataSet.add(ps.getUpdateCount());
                }
                //
                // 接收其它结果
                while (ps.getMoreResults()) {
                    ResultSet resultSet = ps.getResultSet();
                    if (FRAGMENT_SQL_MULTIPLE_QUERIES_FIRST.equalsIgnoreCase(keepType)) {
                        continue;
                    }
                    if (FRAGMENT_SQL_MULTIPLE_QUERIES_LAST.equalsIgnoreCase(keepType)) {
                        resultDataSet.set(0, dataExtractor(hint, resultSet));
                        continue;
                    }
                    if (FRAGMENT_SQL_MULTIPLE_QUERIES_ALL.equalsIgnoreCase(keepType)) {
                        resultDataSet.add(dataExtractor(hint, resultSet));
                        continue;
                    }
                }
                // 返回结果
                if (resultDataSet.size() <= 1) {
                    return resultDataSet.get(0);
                } else {
                    return resultDataSet;
                }
            };
            // 执行查询
            return useJdbcTemplate.execute(queryString, preparedCallback);
        });
    }

    <T> T executeSQL(String sourceName, String sqlString, Object[] paramArrays, SqlQuery<T> sqlQuery) throws SQLException {
        return executeSQL(false, sourceName, sqlString, paramArrays, sqlQuery);
    }

    /** 执行 SQL */
    protected <T> T executeSQL(boolean batch, String sourceName, String sqlString, Object[] paramArrays, SqlQuery<T> sqlQuery) throws SQLException {
        if (this.spiTrigger.hasSpi(FxSqlCheckChainSpi.class)) {
            final FxSqlInfo fxSqlInfo = new FxSqlInfo(batch, sourceName, sqlString, paramArrays);
            final AtomicBoolean doExit = new AtomicBoolean(false);
            this.spiTrigger.chainSpi(FxSqlCheckChainSpi.class, (listener, lastResult) -> {
                if (doExit.get()) {
                    return lastResult;
                }
                int doCheck = listener.doCheck(fxSqlInfo);
                if (doCheck == FxSqlCheckChainSpi.EXIT) {
                    doExit.set(true);
                }
                return lastResult;
            }, fxSqlInfo);
            //
            return sqlQuery.doQuery(fxSqlInfo.getQueryString(), fxSqlInfo.getQueryParams(), this.getJdbcTemplate(sourceName));
        } else {
            return sqlQuery.doQuery(sqlString, paramArrays, this.getJdbcTemplate(sourceName));
        }
    }

    public interface SqlQuery<T> {
        /** 执行 SQL */
        public T doQuery(String querySQL, Object[] params, JdbcTemplate useJdbcTemplate) throws SQLException;
    }

    /** 分析 SQL */
    protected FxQuery analysisSQL(Hints hint, String fragmentString) {
        return DefaultFxQuery.analysisSQL(fragmentString);
    }

    /** 结果转换 */
    protected Object convertResult(Hints hint, List<Map<String, Object>> mapList) {
        String openPackage = hint.getOrDefault(FxHintNames.FRAGMENT_SQL_OPEN_PACKAGE.name(), FxHintNames.FRAGMENT_SQL_OPEN_PACKAGE.getDefaultVal()).toString();
        String caseModule = hint.getOrDefault(FxHintNames.FRAGMENT_SQL_COLUMN_CASE.name(), FxHintNames.FRAGMENT_SQL_COLUMN_CASE.getDefaultVal()).toString();
        if (!FRAGMENT_SQL_COLUMN_CASE_DEFAULT.equalsIgnoreCase(caseModule)) {
            final boolean toUpper = FRAGMENT_SQL_COLUMN_CASE_UPPER.equalsIgnoreCase(caseModule);
            final boolean toLower = FRAGMENT_SQL_COLUMN_CASE_LOWER.equalsIgnoreCase(caseModule);
            final boolean toHump = FRAGMENT_SQL_COLUMN_CASE_HUMP.equalsIgnoreCase(caseModule);
            //
            for (int i = 0; i < mapList.size(); i++) {
                Map<String, Object> newMap = new LinkedHashMap<>();
                mapList.get(i).forEach((key, value) -> {
                    if (toUpper) {
                        newMap.put(key.toUpperCase(), value);
                    } else if (toLower) {
                        newMap.put(key.toLowerCase(), value);
                    } else if (toHump) {
                        newMap.put(StringUdfSource.lineToHump(key.toLowerCase()), value);
                    } else {
                        newMap.put(key, value);
                    }
                });
                mapList.set(i, newMap);
            }
        }
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
    }

    /** 判断是否需要分页 */
    protected boolean usePage(Hints hint) {
        FxHintNames queryByPage = FxHintNames.FRAGMENT_SQL_QUERY_BY_PAGE;
        Object hintOrDefault = hint.getOrDefault(queryByPage.name(), queryByPage.getDefaultVal());
        return FRAGMENT_SQL_QUERY_BY_PAGE_ENABLE.equalsIgnoreCase(hintOrDefault.toString());
    }

    /**
     * 尝试推断SQL语句类型（仅在分页、批量场景下有作用）
     *  - Query 可以执行分页，其它类型语句会退化。
     *  - Insert/Update/Delete 语句之外的：退化为 非批量
     * */
    private static SqlMode evalSqlMode(String fragmentString) throws IOException {
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
                if (tempLine.startsWith("--") || tempLine.startsWith("//")) {
                    continue;
                }
                // 多行注释
                if (tempLine.startsWith("/*")) {
                    if (tempLine.contains("*/")) {
                        tempLine = tempLine.substring(tempLine.indexOf("*/") + 2).trim();// 使用多行注释定义了一个单行注释
                    }
                    if (StringUtils.isBlank(tempLine)) {
                        continue;
                    }
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
            if (tempLine.startsWith("insert") || tempLine.startsWith("replace")) {
                sqlMode = SqlMode.Insert;
            } else if (tempLine.startsWith("update")) {
                sqlMode = SqlMode.Update;
            } else if (tempLine.startsWith("delete")) {
                sqlMode = SqlMode.Delete;
            } else if (tempLine.startsWith("exec")) {
                sqlMode = SqlMode.Procedure;
            } else if (tempLine.startsWith("select")) {
                sqlMode = SqlMode.Query;
            } else if (tempLine.startsWith("create")) {
                sqlMode = SqlMode.Create;
            } else if (tempLine.startsWith("drop")) {
                sqlMode = SqlMode.Drop;
            } else if (tempLine.startsWith("alter")) {
                sqlMode = SqlMode.Alter;
            } else {
                sqlMode = SqlMode.Other;
            }
            break;
        }
        return sqlMode;
    }

    protected Object dataExtractor(Hints hint, ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> tempData = RESULT_EXTRACTOR.get().extractData(resultSet);
        return convertResult(hint, tempData);
    }
}