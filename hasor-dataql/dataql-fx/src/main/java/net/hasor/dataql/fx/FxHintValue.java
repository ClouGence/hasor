package net.hasor.dataql.fx;
import net.hasor.dataql.HintValue;

public interface FxHintValue extends HintValue {
    /**
     * SqlFragment 返回值不拆开，无论返回数据，都以 List/Map 形式返回。
     */
    public static final String FRAGMENT_SQL_OPEN_PACKAGE_OFF    = "off";
    /**
     * SqlFragment 返回值拆分到行，如果返回值是多条记录那么行为和 off 相同。
     *  - 如果是 1条记录，那么返回一个 Object。
     */
    public static final String FRAGMENT_SQL_OPEN_PACKAGE_ROW    = "row";
    /**
     * SqlFragment 返回值拆分到行，如果返回值是多条记录那么行为和 off 相同。
     *  - 如果返回值是 1条记录并且具有多个字段值，那么行为和 row 相同。
     *  - 一条记录中如果只有一个字段，那么会忽略字段名直接返回这个字段的值。
     *  - 如果查询结果为空集合，那么返回 null 值。 */
    public static final String FRAGMENT_SQL_OPEN_PACKAGE_COLUMN = "column";
}
