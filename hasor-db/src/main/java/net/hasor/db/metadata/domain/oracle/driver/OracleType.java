package net.hasor.db.metadata.domain.oracle.driver;
import java.sql.SQLType;

/**
 * 参考 ojdbc8-19.8.0.0.jar
 */
public enum OracleType implements SQLType {
    VARCHAR2("VARCHAR2", OracleTypes.VARCHAR),
    NVARCHAR("NVARCHAR", OracleTypes.NVARCHAR, true),
    NUMBER("NUMBER", OracleTypes.NUMERIC),
    FLOAT("FLOAT", OracleTypes.FLOAT),
    LONG("LONG", OracleTypes.LONGVARCHAR),
    DATE("DATE", OracleTypes.DATE),
    BINARY_FLOAT("BINARY FLOAT", OracleTypes.BINARY_FLOAT),
    BINARY_DOUBLE("BINARY DOUBLE", OracleTypes.BINARY_DOUBLE),
    TIMESTAMP("TIMESTAMP", OracleTypes.TIMESTAMP),
    TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE", OracleTypes.TIMESTAMPTZ),
    TIMESTAMP_WITH_LOCAL_TIME_ZONE("TIMESTAMP WITH LOCAL TIME ZONE", OracleTypes.TIMESTAMPLTZ),
    INTERVAL_YEAR_TO_MONTH("INTERVAL YEAR TO MONTH", OracleTypes.INTERVALYM),
    INTERVAL_DAY_TO_SECOND("INTERVAL DAY TO SECOND", OracleTypes.INTERVALDS),
    PLSQL_BOOLEAN("PLSQL_BOOLEAN", OracleTypes.PLSQL_BOOLEAN),
    RAW("RAW", OracleTypes.BINARY),
    LONG_RAW("LONG RAW", OracleTypes.LONGVARBINARY),
    ROWID("ROWID", OracleTypes.ROWID),
    UROWID("UROWID"),
    CHAR("CHAR", OracleTypes.CHAR),
    NCHAR("NCHAR", OracleTypes.NCHAR, true),
    CLOB("CLOB", OracleTypes.CLOB),
    NCLOB("NCLOB", OracleTypes.NCLOB, true),
    BLOB("BLOB", OracleTypes.BLOB),
    BFILE("BFILE", -13),
    OBJECT("OBJECT", OracleTypes.STRUCT),
    REF("REF", OracleTypes.REF),
    VARRAY("VARRAY", OracleTypes.ARRAY),
    NESTED_TABLE("NESTED_TABLE", OracleTypes.ARRAY),
    ANYTYPE("ANYTYPE", OracleTypes.OPAQUE),
    ANYDATA("ANYDATA", OracleTypes.OPAQUE),
    ANYDATASET("ANYDATASET"),
    XMLTYPE("XMLTYPE", OracleTypes.SQLXML),
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
    SI_TEXTURE("SI_TEXTURE"),
    ;
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
