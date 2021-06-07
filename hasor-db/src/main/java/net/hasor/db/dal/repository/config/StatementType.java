package net.hasor.db.dal.repository.config;
public enum StatementType {
    /** 使用 java.sql.Statement */
    Statement("STATEMENT"),
    /** 使用 java.sql.PreparedStatement */
    Prepared("PREPARED"),
    /** 使用 java.sql.CallableStatement */
    Callable("CALLABLE"),
    ;
    private final String typeName;

    StatementType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static StatementType valueOfCode(String code, StatementType defaultType) {
        for (StatementType tableType : StatementType.values()) {
            if (tableType.typeName.equalsIgnoreCase(code)) {
                return tableType;
            }
        }
        return defaultType;
    }
}
