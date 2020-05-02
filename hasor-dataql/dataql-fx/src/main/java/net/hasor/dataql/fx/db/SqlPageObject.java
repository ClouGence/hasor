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
import net.hasor.dataql.UdfSourceAssembly;
import net.hasor.dataql.fx.db.dialect.SqlPageDialect.BoundSql;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.utils.convert.ConverterUtils;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.hasor.dataql.fx.db.SqlPageQuery.SqlPageQueryConvertResult;

/**
 * 翻页数据，同时负责调用分页的SQL执行分页查询
 * @version : 2014年10月25日
 * @author 赵永春 zyc@hasor.net
 */
public class SqlPageObject implements UdfSourceAssembly {
    /**满足条件的总记录数*/
    private int                       totalCount       = 0;
    /**每页记录数（-1表示无限大）*/
    private int                       pageSize         = -1;
    /**当前页号*/
    private int                       currentPage      = 0;
    //
    private boolean                   totalCountInited = false;
    private SqlPageQuery              sqlPageQuery     = null;
    private SqlPageQueryConvertResult convertResult    = null;

    SqlPageObject(SqlPageQueryConvertResult convertResult, SqlPageQuery sqlPageQuery) {
        this.convertResult = convertResult;
        this.sqlPageQuery = sqlPageQuery;
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
            final BoundSql countBoundSql = this.sqlPageQuery.getCountBoundSql();
            this.totalCount = this.sqlPageQuery.doQuery(con -> {
                String countFxSql = countBoundSql.getSqlString();
                Object[] paramArrays = countBoundSql.getParamMap();
                return new JdbcTemplate(con).queryForInt(countFxSql, paramArrays);
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
            put("currentPage", currentPage());
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
        //
        currentPage((Integer) ConverterUtils.convert(Integer.TYPE, currentPage));
        pageSize((Integer) ConverterUtils.convert(Integer.TYPE, pageSize));
        return true;
    }

    /** 移动到最后一页 */
    public Object data() throws SQLException {
        final BoundSql countBoundSql = this.sqlPageQuery.getPageBoundSql(firstRecordPosition(), pageSize());
        return this.sqlPageQuery.doQuery(con -> {
            String countFxSql = countBoundSql.getSqlString();
            Object[] paramArrays = countBoundSql.getParamMap();
            List<Map<String, Object>> resultData = new JdbcTemplate(con).queryForList(countFxSql, paramArrays);
            return this.convertResult.convertPageResult(resultData);
        });
    }
}