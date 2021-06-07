package net.hasor.db.dal.repository.config;
public enum MultipleResultsType {
    /** 多结果，保留第一个结果 */
    FIRST("FIRST"),
    /** 多结果，保留最后结果 */
    LAST("LAST"),
    /** 多结果，全部保留 */
    ALL("ALL");
    private final String typeName;

    MultipleResultsType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static MultipleResultsType valueOfCode(String code, MultipleResultsType defaultType) {
        for (MultipleResultsType tableType : MultipleResultsType.values()) {
            if (tableType.typeName.equalsIgnoreCase(code)) {
                return tableType;
            }
        }
        return defaultType;
    }
}