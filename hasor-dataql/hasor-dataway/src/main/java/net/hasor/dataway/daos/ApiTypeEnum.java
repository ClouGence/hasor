package net.hasor.dataway.daos;
public enum ApiTypeEnum {
    DataQL("DataQL"), SQL("SQL");
    private final String typeString;

    ApiTypeEnum(String typeStr) {
        this.typeString = typeStr;
    }

    public static ApiTypeEnum typeOf(Object codeType) {
        if (codeType == null) {
            return null;
        }
        String target = codeType.toString();
        for (ApiTypeEnum typeEnum : values()) {
            if (String.valueOf(typeEnum.typeString).equalsIgnoreCase(target)) {
                return typeEnum;
            }
            if (typeEnum.name().equalsIgnoreCase(target)) {
                return typeEnum;
            }
        }
        return null;
    }

    public String typeString() {
        return this.typeString;
    }
}