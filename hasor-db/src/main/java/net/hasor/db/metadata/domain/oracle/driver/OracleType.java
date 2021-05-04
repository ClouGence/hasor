package net.hasor.db.metadata.domain.oracle.driver;
import java.sql.SQLType;

public enum OracleType implements SQLType {
    VARCHAR2("VARCHAR2", 12),
    NVARCHAR("NVARCHAR", -9, true),
    NUMBER("NUMBER", 2),
    FLOAT("FLOAT", 6),
    LONG("LONG", -1),
    DATE("DATE", 91),
    BINARY_FLOAT("BINARY FLOAT", 100),
    BINARY_DOUBLE("BINARY DOUBLE", 101),
    TIMESTAMP("TIMESTAMP", 93),
    TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE", -101),
    TIMESTAMP_WITH_LOCAL_TIME_ZONE("TIMESTAMP WITH LOCAL TIME ZONE", -102),
    INTERVAL_YEAR_TO_MONTH("INTERVAL YEAR TO MONTH", -103),
    INTERVAL_DAY_TO_SECOND("INTERVAL DAY TO SECOND", -104),
    PLSQL_BOOLEAN("PLSQL_BOOLEAN", 252),
    RAW("RAW", -2),
    LONG_RAW("LONG RAW", -4),
    ROWID("ROWID", -8),
    UROWID("UROWID"),
    CHAR("CHAR", 1),
    NCHAR("NCHAR", -15, true),
    CLOB("CLOB", 2005),
    NCLOB("NCLOB", 2011, true),
    BLOB("BLOB", 2004),
    BFILE("BFILE", -13),
    OBJECT("OBJECT", 2002),
    REF("REF", 2006),
    VARRAY("VARRAY", 2003),
    NESTED_TABLE("NESTED_TABLE", 2003),
    ANYTYPE("ANYTYPE", 2007),
    ANYDATA("ANYDATA", 2007),
    ANYDATASET("ANYDATASET"),
    XMLTYPE("XMLTYPE", 2009),
    HTTPURITYPE("HTTPURITYPE"),
    XDBURITYPE("XDBURITYPE"),
    DBURITYPE("DBURITYPE"),
    SDO_GEOMETRY("SDO_GEOMETRY"),
    SDO_TOPO_GEOMETRY("SDO_TOPO_GEOMETRY"),
    SDO_GEORASTER("SDO_GEORASTER"),
    ORDAUDIO("ORDAUDIO"),
    ORDDICOM("ORDDICOM"),
    ORDDOC("ORDDOC"),
    ORDIMAGE("ORDIMAGE"),
    ORDVIDEO("ORDVIDEO"),
    SI_AVERAGE_COLOR("SI_AVERAGE_COLOR"),
    SI_COLOR("SI_COLOR"),
    SI_COLOR_HISTOGRAM("SI_COLOR_HISTOGRAM"),
    SI_FEATURE_LIST("SI_FEATURE_LIST"),
    SI_POSITIONAL_COLOR("SI_POSITIONAL_COLOR"),
    SI_STILL_IMAGE("SI_STILL_IMAGE"),
    SI_TEXTURE("SI_TEXTURE");
    private final boolean isSupported;
    private final String  typeName;
    private final int     code;
    private final boolean isNationalCharacterSet;

    public static OracleType toOracleType(SQLType sqlType) {
        return sqlType instanceof OracleType ? (OracleType) sqlType : null;
    }

    public static OracleType toOracleType(int oracleType) {
        for (OracleType type : values()) {
            if (type.getVendorTypeNumber() == oracleType) {
                return type;
            }
        }
        return null;
    }

    OracleType(String oracleType) {
        this.isSupported = false;
        this.typeName = oracleType;
        this.code = -2147483648;
        this.isNationalCharacterSet = false;
    }

    OracleType(String oracleType, int jdbcType) {
        this.isSupported = true;
        this.typeName = oracleType;
        this.code = jdbcType;
        this.isNationalCharacterSet = false;
    }

    OracleType(String oracleType, int jdbcType, boolean var5) {
        this.isSupported = true;
        this.typeName = oracleType;
        this.code = jdbcType;
        this.isNationalCharacterSet = var5;
    }

    public String getName() {
        return this.typeName;
    }

    public String getVendor() {
        return "Oracle Database";
    }

    public Integer getVendorTypeNumber() {
        return this.code;
    }

    public boolean isNationalCharacterSet() {
        return this.isNationalCharacterSet;
    }

    public boolean isSupported() {
        return this.isSupported;
    }
}
