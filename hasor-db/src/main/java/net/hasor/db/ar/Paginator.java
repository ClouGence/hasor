/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.db.ar;
/**
 * 翻页
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class Paginator {
    /**满足条件的总记录数*/
    private int totalCount  = 0;
    /**每页记录数（-1表示无限大）*/
    private int pageSize    = 15;
    /**当前页号*/
    private int currentPage = 1;
    //    //
    //    /** 排序字段 */
    //    private String  sortField   = "";
    //    /** 排序方式 */
    //    private OrderBy orderBy     = OrderBy.ASC;
    //    //
    //    /**排序方式*/
    //    public static enum OrderBy {
    //        ASC, DESC
    //    }
    //
    //
    //
    /**获取分页的页大小。*/
    public int getPageSize() {
        return this.pageSize;
    }
    /**设置分页的页大小。*/
    public void setPageSize(int pageSize) {
        if (pageSize < 1)
            pageSize = 1;
        this.pageSize = pageSize;
    }
    /**获取记录总数。*/
    public int getTotalCount() {
        return this.totalCount;
    }
    /**设置记录总数。*/
    public void setTotalCount(int totalCount) {
        if (totalCount < 0)
            totalCount = 0;
        this.totalCount = totalCount;
    }
    //
    /**当前是否是第一页。*/
    public boolean isFirstPage() {
        return getCurrentPage() == 1;
    }
    /**判断是否还具备上一页。*/
    public boolean hasPreviousPage() {
        return !isFirstPage();
    }
    /**获取上一页页号。*/
    public int getPreviousPage() {
        int back = getCurrentPage() - 1;
        return (back <= 0) ? 1 : back;
    }
    /**获取下一页页号。*/
    public int getNextPage() {
        int back = getCurrentPage() + 1;
        return (back > getTotalPage()) ? getTotalPage() : back;
    }
    /**判断是否还具备下一页。*/
    public boolean hasNextPage() {
        return !isLastPage();
    }
    /**当前是否是最后一页。*/
    public boolean isLastPage() {
        return getTotalPage() <= getCurrentPage();
    }
    /**取当前页号。*/
    public int getCurrentPage() {
        return this.currentPage;
    }
    /**设置前页号。*/
    public void setCurrentPage(int currentPage) {
        if (currentPage < 1)
            currentPage = 1;
        this.currentPage = currentPage;
    }
    /**获取总页数。*/
    public int getTotalPage() {
        int pgSize = getPageSize();
        int result = 1;
        if (pgSize > 0) {
            int totalCount = getTotalCount();
            result = getTotalCount() / pgSize;
            if ((totalCount == 0) || ((totalCount % pgSize) != 0)) {
                result++;
            }
        }
        return result;
    }
    /**获取本页第一个记录的索引位置。*/
    public int getFirstItem() {
        int cPage = getCurrentPage();
        if (cPage == 1) {
            return 1; // 第一页开始当然是第 1 条记录
        }
        cPage--;
        int pgSize = getPageSize();
        return (pgSize * cPage) + 1;
    }
    /**获取本页最后一个记录的索引位置。*/
    public int getLastItem() {
        int cPage = getCurrentPage();
        int pgSize = getPageSize();
        int assumeLast = pgSize * cPage;
        int totalCount = getTotalCount();
        return (assumeLast > totalCount) ? totalCount : assumeLast;
    }
}