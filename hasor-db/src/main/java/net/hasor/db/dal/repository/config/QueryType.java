package net.hasor.db.dal.repository.config;
public enum QueryType {
    /** Insert 类型 */
    Insert("insert"),
    /** Delete 类型 */
    Delete("delete"),
    /** Update 类型 */
    Update("update"),
    /** 查询类型 类型 */
    Query("select"),
    /** 调用存储过程 */
    Callable("callable"),
    /** Sql 片段，可以被 include */
    Segment("sql"),
    ;
    private final String xmlTag;

    public String getXmlTag() {
        return this.xmlTag;
    }

    QueryType(String xmlTag) {
        this.xmlTag = xmlTag;
    }

    public static QueryType valueOfTag(String xmlTag) {
        for (QueryType tableType : QueryType.values()) {
            if (tableType.xmlTag.equalsIgnoreCase(xmlTag)) {
                return tableType;
            }
        }
        return null;
    }
}
