package net.hasor.dataway.daos;
public enum ApiStatusEnum {
    Editor(0),      //
    Published(1),   //
    Changes(2),     //
    Disable(3),     //
    Delete(-1);     //
    private final int typeNum; //

    ApiStatusEnum(int typeNum) {
        this.typeNum = typeNum;
    }

    public static ApiStatusEnum typeOf(Object codeType) {
        if (codeType == null) {
            return null;
        }
        String target = codeType.toString();
        for (ApiStatusEnum typeEnum : values()) {
            if (String.valueOf(typeEnum.typeNum).equalsIgnoreCase(target)) {
                return typeEnum;
            }
            if (typeEnum.name().equalsIgnoreCase(target)) {
                return typeEnum;
            }
        }
        return null;
    }

    public int typeNum() {
        return typeNum;
    }
}