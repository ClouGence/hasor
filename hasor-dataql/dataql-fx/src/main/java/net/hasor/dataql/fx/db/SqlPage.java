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
import net.hasor.db.jdbc.SqlParameterSource;

import java.util.List;
import java.util.Map;

/**
 * 翻页
 * @version : 2014年10月25日
 * @author 赵永春 zyc@hasor.net
 */
public class SqlPage implements UdfSourceAssembly {
    /**满足条件的总记录数*/
    private int            totalCount       = 0;
    /**每页记录数（-1表示无限大）*/
    private int            pageSize         = 15;
    /**当前页号*/
    private int            currentPage      = 0;
    //
    private boolean        totalCountInited = false;
    private SqlPageDialect pageDialect      = null;

    SqlPage(SqlPageDialect pageDialect) {
        this.pageDialect = pageDialect;
        this.totalCountInited = false;
    }

    /** 获取分页的页大小 */
    public int pageSize() {
        return this.pageSize;
    }

    /** 设置分页的页大小 */
    public int pageSize(int pageSize) {
        if (pageSize < 1) {
            pageSize = 1;
        }
        this.pageSize = pageSize;
        return this.pageSize();
    }

    /** 获取记录总数 */
    public int totalCount() {
        if (!this.totalCountInited) {
            String countFxSql = null;//this.pageDialect.getCountSql().ge();
            SqlParameterSource paramMap = null;// this.sqlPageQuery.getParamMap();
            this.totalCountInited = true;
        }
        return this.totalCount;
    }

    /** 获取总页数 */
    public int totalPage() {
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
    public int currentPage() {
        return this.currentPage;
    }

    /** 设置前页号 */
    public int currentPage(int currentPage) {
        if (currentPage < 0) {
            currentPage = 0;
        }
        this.currentPage = currentPage;
        return currentPage();
    }

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
    public int nextPage() {
        int back = currentPage() + 1;
        int nextPage = Math.min(back, totalPage());
        return currentPage(nextPage);
    }

    /** 移动到最后一页 */
    public int lastPage() {
        return currentPage(totalPage());
    }

    /** 移动到最后一页 */
    public List<Map<String, Object>> data() {
        String queryFxSql = null;//this.sqlPageQuery.getQueryFxSql();
        SqlParameterSource paramMap = null;// this.sqlPageQuery.getParamMap();
        return null;
    }

    /** 获取本页第一个记录的索引位置 */
    private int firstRecordPosition() {
        int cPage = currentPage();
        int pgSize = pageSize();
        return (pgSize * cPage);
    }

    /** 获取本页最后一个记录的索引位置 */
    private int lastRecordPosition() {
        int cPage = currentPage();
        int pgSize = pageSize();
        int assumeLast = pgSize + (pgSize * cPage);
        int totalCount = totalCount();
        return Math.min(assumeLast, totalCount);
    }
}