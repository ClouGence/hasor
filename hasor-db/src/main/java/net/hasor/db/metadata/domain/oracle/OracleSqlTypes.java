package net.hasor.db.metadata.domain.oracle;
import net.hasor.db.metadata.SqlType;

import java.sql.JDBCType;
import java.sql.SQLType;
import java.sql.Types;

/**
 * 参考 ojdbc8-19.8.0.0.jar
 * @version : 2021-03-30
 * @author 赵永春 (zyc@hasor.net)
 */
public enum OracleSqlTypes implements SQLType, SqlType {
    VARCHAR2("VARCHAR2", Types.VARCHAR),
    NVARCHAR("NVARCHAR", Types.NVARCHAR, true),
    NVARCHAR2("NVARCHAR2", Types.NVARCHAR, true),
    NUMBER("NUMBER", Types.NUMERIC),
    FLOAT("FLOAT", Types.FLOAT),
    LONG("LONG", Types.LONGVARCHAR),
    DATE("DATE", Types.DATE),
    BINARY_FLOAT("BINARY FLOAT", 100),
    BINARY_DOUBLE("BINARY DOUBLE", 101),
    TIMESTAMP("TIMESTAMP", Types.TIMESTAMP),
    TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE", -101),
    TIMESTAMP_WITH_LOCAL_TIME_ZONE("TIMESTAMP WITH LOCAL TIME ZONE", -102),
    INTERVAL_YEAR_TO_MONTH("INTERVAL YEAR TO MONTH", -103),
    INTERVAL_DAY_TO_SECOND("INTERVAL DAY TO SECOND", -104),
    PLSQL_BOOLEAN("PLSQL_BOOLEAN", 252),
    RAW("RAW", Types.BINARY),
    LONG_RAW("LONG RAW", Types.LONGVARBINARY),
    ROWID("ROWID", Types.ROWID),
    UROWID("UROWID"),
    CHAR("CHAR", Types.CHAR),
    NCHAR("NCHAR", Types.NCHAR, true),
    CLOB("CLOB", Types.CLOB),
    NCLOB("NCLOB", Types.NCLOB, true),
    BLOB("BLOB", Types.BLOB),
    BFILE("BFILE", -13),
    OBJECT("OBJECT", Types.STRUCT),
    REF("REF", Types.REF),
    VARRAY("VARRAY", Types.ARRAY),
    NESTED_TABLE("NESTED_TABLE", Types.ARRAY),
    ANYTYPE("ANYTYPE", 2007),
    ANYDATA("ANYDATA", 2007),
    ANYDATASET("ANYDATASET"),
    XMLTYPE("XMLTYPE", Types.SQLXML),
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
    private final JDBCType jdbcType;
    private final boolean  isSupported;
    private final String   typeName;
    private final int      code;
    private final boolean  isNationalCharacterSet;

    public static OracleSqlTypes toOracleType(SQLType sqlType) {
        return sqlType instanceof OracleSqlTypes ? (OracleSqlTypes) sqlType : null;
    }

    public static OracleSqlTypes toOracleType(int jdbcType) {
        Object[] var1 = values();
        OracleSqlTypes var2 = null;
        for (Object o : var1) {
            OracleSqlTypes var4 = (OracleSqlTypes) o;
            if (var4.getVendorTypeNumber() == jdbcType) {
                var2 = var4;
                break;
            }
        }
        return var2;
    }

    public static SqlType toOracleType(String dataType) {
        if (dataType.startsWith("TIMESTAMP")) {
            if (dataType.contains("ZONE")) {
                if (dataType.contains("LOCAL")) {
                    return TIMESTAMP_WITH_LOCAL_TIME_ZONE;
                }
                return TIMESTAMP_WITH_TIME_ZONE;
            }
            return TIMESTAMP;
        } else if (dataType.startsWith("INTERVAL")) {
            if (dataType.contains("DAY")) {
                return INTERVAL_DAY_TO_SECOND;
            }
            if (dataType.contains("YEAR")) {
                return INTERVAL_YEAR_TO_MONTH;
            }
        } else if (dataType.startsWith("BINARY_FLOAT")) {
            return BINARY_FLOAT;
        } else if (dataType.startsWith("BINARY_DOUBLE")) {
            return BINARY_DOUBLE;
        }
        //
        for (OracleSqlTypes type : OracleSqlTypes.values()) {
            if (type.getCodeKey().equalsIgnoreCase(dataType)) {
                return type;
            }
        }
        return null;
    }

    private static JDBCType jdbcType(int jdbcType) {
        try {
            return JDBCType.valueOf(jdbcType);
        } catch (Exception e) {
            return null;
        }
    }

    OracleSqlTypes(String oracleType) {
        this.isSupported = false;
        this.typeName = oracleType;
        this.code = -2147483648;
        this.jdbcType = null;
        this.isNationalCharacterSet = false;
    }

    OracleSqlTypes(String oracleType, int jdbcType) {
        this.isSupported = true;
        this.typeName = oracleType;
        this.code = jdbcType;
        this.jdbcType = jdbcType(jdbcType);
        this.isNationalCharacterSet = false;
    }

    OracleSqlTypes(String oracleType, int jdbcType, boolean var5) {
        this.isSupported = true;
        this.typeName = oracleType;
        this.code = jdbcType;
        this.jdbcType = jdbcType(jdbcType);
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

    @Override
    public String getCodeKey() {
        return this.getName();
    }

    @Override
    public JDBCType getJdbcType() {
        return this.jdbcType;
    }
}