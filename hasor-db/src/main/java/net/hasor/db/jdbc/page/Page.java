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
package net.hasor.db.jdbc.page;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 分页
 * @version : 2021-02-04
 * @author 赵永春 (zyc@hasor.net)
 */
public interface Page {
    public int getPageSize();

    /** 设置分页的页大小 */
    public void setPageSize(int pageSize);

    /**取当前页号 */
    public int getCurrentPage();

    /** 设置前页号 */
    public void setCurrentPage(int currentPage);

    public int getPageNumberOffset();

    public void setPageNumberOffset(int pageNumberOffset);

    /** 获取本页第一个记录的索引位置 */
    public int getFirstRecordPosition();

    /** 获取总页数 */
    public int getTotalPage() throws SQLException;

    /** 获取记录总数 */
    public int getTotalCount() throws SQLException;

    /** 移动到第一页 */
    public default void firstPage() {
        setCurrentPage(0);
    }

    /** 移动到上一页 */
    public default void previousPage() {
        setCurrentPage(getCurrentPage() - 1);
    }

    /** 移动到下一页 */
    public default void nextPage() {
        setCurrentPage(getCurrentPage() + 1);
    }

    /** 移动到最后一页 */
    public default void lastPage() throws SQLException {
        setCurrentPage(getTotalPage());
    }

    /** 获取分页的页大小 */
    public default Map<String, Object> toPageInfo() throws SQLException {
        return new LinkedHashMap<String, Object>() {{
            put("enable", getPageSize() > 0);
            put("pageSize", getPageSize());
            put("totalCount", getTotalCount());
            put("totalPage", getTotalPage());
            put("currentPage", getCurrentPage());
            put("recordPosition", getFirstRecordPosition());
        }};
    }
}
