package net.hasor.db.dal.repository.config;
//FORWARD_ONLY，SCROLL_SENSITIVE, SCROLL_INSENSITIVE 或 DEFAULT（等价于 unset） 中的一个，默认值为 unset （依赖数据库驱动）。
public enum ResultSetType {
    FORWARD_ONLY("FORWARD_ONLY"),
    SCROLL_SENSITIVE("SCROLL_SENSITIVE"),
    SCROLL_INSENSITIVE("SCROLL_INSENSITIVE"),
    DEFAULT("DEFAULT"),
    ;
    private final String typeName;

    ResultSetType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static ResultSetType valueOfCode(String code, ResultSetType defaultType) {
        for (ResultSetType tableType : ResultSetType.values()) {
            if (tableType.typeName.equalsIgnoreCase(code)) {
                return tableType;
            }
        }
        return defaultType;
    }
}
