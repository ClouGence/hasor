package net.hasor.db.page;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

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
    public int totalCount() throws SQLException;

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
            put("totalCount", totalCount());
            put("totalPage", getTotalPage());
            put("currentPage", getCurrentPage());
            put("recordPosition", getFirstRecordPosition());
        }};
    }
}