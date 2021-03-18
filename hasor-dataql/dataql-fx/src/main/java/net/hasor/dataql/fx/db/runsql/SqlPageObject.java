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
import net.hasor.dataql.Hints;
import net.hasor.dataql.UdfSourceAssembly;
import net.hasor.db.lambda.dialect.BoundSql;
import net.hasor.db.lambda.dialect.SqlDialect;
import net.hasor.utils.convert.ConverterUtils;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.hasor.dataql.fx.FxHintNames.FRAGMENT_SQL_DATA_SOURCE;
import static net.hasor.dataql.fx.FxHintNames.FRAGMENT_SQL_QUERY_BY_PAGE_NUMBER_OFFSET;

/**
 * 翻页数据，同时负责调用分页的SQL执行分页查询
 * @version : 2014年10月25日
 * @author 赵永春 zyc@hasor.net
 */
class SqlPageObject implements UdfSourceAssembly {
    /**满足条件的总记录数*/
    private int         totalCount        = 0;
    /**每页记录数（-1表示无限大）*/
    private int         pageSize          = -1;
    /**当前页号*/
    private int         currentPage       = 0;
    //
    private boolean     totalCountInited  = false;
    private int         pageNumberOffset  = 0;
    private String      useDataSource     = null;
    private Hints       hints             = null;
    private BoundSql    originalBoundSql  = null;
    private SqlDialect  pageDialect       = null;
    private SqlFragment sourceSqlFragment = null;

    SqlPageObject(                          //
            Hints hints,                    // 查询包含的 Hint
            BoundSql originalBoundSql,      // 查询BoundSql
            SqlDialect pageDialect,         // 分页方言服务
            SqlFragment sourceSqlFragment   // 用于执行分页查询的服务
    ) {
        this.pageNumberOffset = (int) ConverterUtils.convert(String.valueOf(hints.getOrDefault(//
                FRAGMENT_SQL_QUERY_BY_PAGE_NUMBER_OFFSET.name(),//
                FRAGMENT_SQL_QUERY_BY_PAGE_NUMBER_OFFSET.getDefaultVal())//
        ), Integer.TYPE);
        //
        this.useDataSource = hints.getOrDefault(FRAGMENT_SQL_DATA_SOURCE.name(), "").toString();
        this.hints = hints;
        this.originalBoundSql = originalBoundSql;
        this.pageDialect = pageDialect;
        this.sourceSqlFragment = sourceSqlFragment;
        this.totalCountInited = false;
    }

    private int pageSize() {
        return this.pageSize;
    }

    /** 设置分页的页大小 */
    private int pageSize(int pageSize) {
        if (pageSize < 1) {
            pageSize = 1;
        }
        this.pageSize = pageSize;
        return this.pageSize();
    }

    /** 获取记录总数 */
    private int totalCount() throws SQLException {
        if (!this.totalCountInited) {
            // 准备SQL和执行的参数
            BoundSql countBoundSql = this.pageDialect.countSql(this.originalBoundSql);
            String countFxSql = countBoundSql.getSqlString();
            Object[] countParams = countBoundSql.getArgs();
            // 通过 doQuery 方法来执行SQL。
            this.totalCount = this.sourceSqlFragment.executeSQL(//
                    this.useDataSource, //
                    countFxSql,         //
                    countParams,        //
                    (querySQL, params, useJdbcTemplate) -> {
                        // 不直接使用 countFxSql, paramArrays 的原因是 doQuery 被调用的时会执行 FxSqlInterceptorChainSpi 拦截器。
                        return useJdbcTemplate.queryForInt(querySQL, params);
                    });
            this.totalCountInited = true;
        }
        return this.totalCount;
    }

    /** 获取总页数 */
    private int totalPage() throws SQLException {
        int pgSize = pageSize();
        int result = 1;
        if (pgSize > 0) {
            int totalCount = totalCount();
            result = totalCount() / pgSize;
            if ((totalCount == 0) || ((totalCount % pgSize) != 0)) {
                result++;
            }
        }
        return result;
    }

    /**取当前页号 */
    private int currentPage() {
        return this.currentPage;
    }

    /** 设置前页号 */
    private int currentPage(int currentPage) {
        if (currentPage < 0) {
            currentPage = 0;
        }
        this.currentPage = currentPage;
        return currentPage();
    }

    /** 获取本页第一个记录的索引位置 */
    private int firstRecordPosition() {
        int pgSize = pageSize();
        if (pgSize < 0) {
            return 0;
        }
        return (pgSize * currentPage());
    }
    // ----------------------------------------------------------------------------------

    /** 移动到第一页 */
    public int firstPage() {
        return currentPage(0);
    }

    /** 移动到上一页 */
    public int previousPage() {
        int back = currentPage() - 1;
        int previousPage = Math.max(back, 0);
        return currentPage(previousPage);
    }

    /** 移动到下一页 */
    public int nextPage() throws SQLException {
        int back = currentPage() + 1;
        int nextPage = Math.min(back, totalPage());
        return currentPage(nextPage);
    }

    /** 移动到最后一页 */
    public int lastPage() throws SQLException {
        return currentPage(totalPage());
    }

    /** 获取分页的页大小 */
    public Map<String, Object> pageInfo() throws SQLException {
        return new LinkedHashMap<String, Object>() {{
            put("enable", pageSize() > 0);
            put("pageSize", pageSize());
            put("totalCount", totalCount());
            put("totalPage", totalPage());
            put("currentPage", currentPage() + pageNumberOffset);
            put("recordPosition", firstRecordPosition());
        }};
    }
    // ----------------------------------------------------------------------------------

    /** 获取分页的页大小 */
    public boolean setPageInfo(Map<String, Object> pageInfo) {
        if (pageInfo == null || pageInfo.isEmpty()) {
            return false;
        }
        Object currentPage = pageInfo.get("currentPage");
        Object pageSize = pageInfo.get("pageSize");
        if (currentPage == null && pageSize == null) {
            return false;
        }
        //FRAGMENT_SQL_QUERY_BY_PAGE_NUMBER_OFFSET
        currentPage(((Integer) ConverterUtils.convert(Integer.TYPE, currentPage) - this.pageNumberOffset));
        pageSize((Integer) ConverterUtils.convert(Integer.TYPE, pageSize));
        return true;
    }

    /** 移动到最后一页 */
    public Object data() throws SQLException {
        BoundSql boundSql = null;
        if (pageSize() < 0) {
            boundSql = this.originalBoundSql;// 如果分页的页码小于0  -> 那么查询所有数据
        } else {
            // 如果分页的页码不等于0  -> 那么执行分页查询
            boundSql = this.pageDialect.pageSql(this.originalBoundSql, firstRecordPosition(), pageSize());
        }
        // 通过 doQuery 方法来执行SQL。
        return this.sourceSqlFragment.executeSQL(//
                this.useDataSource,     //
                boundSql.getSqlString(),//
                boundSql.getArgs(),     //
                (querySQL, params, useJdbcTemplate) -> {
                    // 不直接使用 countFxSql, paramArrays 的原因是 doQuery 被调用的时会执行 FxSqlInterceptorChainSpi 拦截器。
                    List<Map<String, Object>> resultData = useJdbcTemplate.queryForList(querySQL, params);
                    return sourceSqlFragment.convertResult(hints, resultData);
                });
    }
}
