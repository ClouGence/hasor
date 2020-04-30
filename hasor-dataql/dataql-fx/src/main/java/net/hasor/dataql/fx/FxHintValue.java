package net.hasor.dataql.fx;
import net.hasor.dataql.HintValue;

public interface FxHintValue extends HintValue {
    /**
     * SqlFragment 返回值不拆开，无论返回数据，都以 List/Map 形式返回。
     */
    public static final String FRAGMENT_SQL_OPEN_PACKAGE_OFF      = "off";
    /**
     * SqlFragment 返回值拆分到行，如果返回值是多条记录那么行为和 off 相同。
     *  - 当返回 0 或 1 条记录时，自动解开最外层的 List，返回一个 Object。
     */
    public static final String FRAGMENT_SQL_OPEN_PACKAGE_ROW      = "row";
    /**
     * SqlFragment 返回值拆分到行，如果返回值是多条记录那么行为和 off 相同。
     *  - 如果返回值是 1条记录并且具有多个字段值，那么行为和 row 相同。
     *  - 一条记录中如果只有一个字段，那么会忽略字段名直接返回这个字段的值。
     *  - 如果查询结果为空集合，那么返回 null 值。 */
    public static final String FRAGMENT_SQL_OPEN_PACKAGE_COLUMN   = "column";
    /**
     * SqlFragment 在执行 select 语句时采用分页模式执行，获取数据的步骤：1先获取查询对象，2.设置分页参数，3.获取分页之后的数据。
     */
    public static final String FRAGMENT_SQL_QUERY_BY_PAGE_ENABLE  = "true";
    /**
     * SqlFragment 在执行 select 语句时不分页，获取数据的步骤：1先获取查询对象，2.获取SQL执行的数据。
     */
    public static final String FRAGMENT_SQL_QUERY_BY_PAGE_DISABLE = "false";
    /**
     * SqlFragment 返回的列信息,全部列名保持大小写敏感。
     */
    public static final String FRAGMENT_SQL_COLUMN_CASE_DEFAULT   = "default";
    /**
     * SqlFragment 全部列名保持大写，如果在转换过程中发生冲突，那么会产生覆盖问题。
     */
    public static final String FRAGMENT_SQL_COLUMN_CASE_UPPER     = "upper";
    /**
     * SqlFragment 全部列名保持小写，如果在转换过程中发生冲突，那么会产生覆盖问题。
     */
    public static final String FRAGMENT_SQL_COLUMN_CASE_LOWER     = "lower";
    /**
     * SqlFragment 返回的列信息,全部列名做一次驼峰转换。如：goods_id => goodsId、GOODS_id => goodsId。
     */
    public static final String FRAGMENT_SQL_COLUMN_CASE_HUMP      = "hump";
}