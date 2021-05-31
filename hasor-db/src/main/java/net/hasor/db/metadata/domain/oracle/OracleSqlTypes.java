package net.hasor.db.metadata.domain.oracle;
import net.hasor.db.metadata.SqlType;
import net.hasor.db.metadata.domain.oracle.driver.OracleType;

import java.sql.JDBCType;

/**
 * 参考 ojdbc8-19.8.0.0.jar
 * @version : 2021-03-30
 * @author 赵永春 (zyc@hasor.net)
 */
public enum OracleSqlTypes implements SqlType {
    CHAR("CHAR", OracleType.CHAR),
    NCHAR("NCHAR", OracleType.NCHAR),
    VARCHAR2("VARCHAR2", OracleType.VARCHAR2),
    NVARCHAR("NVARCHAR", OracleType.NVARCHAR),
    NVARCHAR2("NVARCHAR2", OracleType.NVARCHAR),
    LONG("LONG", OracleType.LONG),
    //
    NUMBER("NUMBER", OracleType.NUMBER),
    FLOAT("FLOAT", OracleType.FLOAT),
    BINARY_FLOAT("BINARY FLOAT", OracleType.BINARY_FLOAT),
    BINARY_DOUBLE("BINARY DOUBLE", OracleType.BINARY_DOUBLE),
    //
    CLOB("CLOB", OracleType.CLOB),
    NCLOB("NCLOB", OracleType.NCLOB),
    BLOB("BLOB", OracleType.BLOB),
    BFILE("BFILE", OracleType.BFILE),
    //
    DATE("DATE", OracleType.DATE),
    TIMESTAMP("TIMESTAMP", OracleType.TIMESTAMP),
    TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE", OracleType.TIMESTAMP_WITH_TIME_ZONE),
    TIMESTAMP_WITH_LOCAL_TIME_ZONE("TIMESTAMP WITH LOCAL TIME ZONE", OracleType.TIMESTAMP_WITH_LOCAL_TIME_ZONE),
    INTERVAL_YEAR_TO_MONTH("INTERVAL YEAR TO MONTH", OracleType.INTERVAL_YEAR_TO_MONTH),
    INTERVAL_DAY_TO_SECOND("INTERVAL DAY TO SECOND", OracleType.INTERVAL_DAY_TO_SECOND),
    //
    RAW("RAW", OracleType.RAW),
    LONG_RAW("LONG RAW", OracleType.LONG_RAW),
    //
    ROWID("ROWID", OracleType.ROWID),
    UROWID("UROWID", OracleType.UROWID),
    //
    OBJECT("OBJECT", OracleType.OBJECT),
    REF("REF", OracleType.REF),
    VARRAY("VARRAY", OracleType.VARRAY),
    NESTED_TABLE("NESTED_TABLE", OracleType.NESTED_TABLE),
    //
    PLSQL_BOOLEAN("PLSQL_BOOLEAN", OracleType.PLSQL_BOOLEAN),
    ANYTYPE("ANYTYPE", OracleType.ANYTYPE),
    ANYDATA("ANYDATA", OracleType.ANYDATA),
    ANYDATASET("ANYDATASET", OracleType.ANYDATASET),
    //
    XMLTYPE("XMLTYPE", OracleType.XMLTYPE),
    HTTPURITYPE("HTTPURITYPE", OracleType.HTTPURITYPE),
    XDBURITYPE("XDBURITYPE", OracleType.XDBURITYPE),
    DBURITYPE("DBURITYPE", OracleType.DBURITYPE),
    //
    SDO_GEOMETRY("SDO_GEOMETRY", OracleType.SDO_GEOMETRY),
    SDO_TOPO_GEOMETRY("SDO_TOPO_GEOMETRY", OracleType.SDO_TOPO_GEOMETRY),
    SDO_GEORASTER("SDO_GEORASTER", OracleType.SDO_GEORASTER),
    //
    ORDAUDIO("ORDAUDIO", OracleType.ORDAUDIO),
    ORDDICOM("ORDDICOM", OracleType.ORDDICOM),
    ORDDOC("ORDDOC", OracleType.ORDDOC),
    ORDIMAGE("ORDIMAGE", OracleType.ORDIMAGE),
    ORDVIDEO("ORDVIDEO", OracleType.ORDVIDEO),
    SI_AVERAGE_COLOR("SI_AVERAGE_COLOR", OracleType.SI_AVERAGE_COLOR),
    SI_COLOR("SI_COLOR", OracleType.SI_COLOR),
    SI_COLOR_HISTOGRAM("SI_COLOR_HISTOGRAM", OracleType.SI_COLOR_HISTOGRAM),
    SI_FEATURE_LIST("SI_FEATURE_LIST", OracleType.SI_FEATURE_LIST),
    SI_POSITIONAL_COLOR("SI_POSITIONAL_COLOR", OracleType.SI_POSITIONAL_COLOR),
    SI_STILL_IMAGE("SI_STILL_IMAGE", OracleType.SI_STILL_IMAGE),
    SI_TEXTURE("SI_TEXTURE", OracleType.SI_TEXTURE),
    ;
    private final String   codeKey;
    private final JDBCType jdbcType;
    private final Integer  jdbcTypeNum;

    OracleSqlTypes(String codeKey, OracleType oracleType) {
        this.codeKey = codeKey;
        this.jdbcType = toJdbcType(oracleType);
        this.jdbcTypeNum = (this.jdbcType != null) ? this.jdbcType.getVendorTypeNumber() : null;
    }

    public static OracleSqlTypes valueOfCode(String code) {
        for (OracleSqlTypes tableType : OracleSqlTypes.values()) {
            if (tableType.codeKey.equalsIgnoreCase(code)) {
                return tableType;
            }
        }
        return null;
    }

    @Override
    public String getCodeKey() {
        return this.codeKey;
    }

    @Override
    public Integer getJdbcType() {
        return this.jdbcTypeNum;
    }

    @Override
    public JDBCType toJDBCType() {
        return this.jdbcType;
    }

    public static JDBCType toJdbcType(OracleType oracleType) {
        if (oracleType == null) {
            return null;
        }
        switch (oracleType) {
            case UROWID:
            case ROWID:
                return JDBCType.ROWID;
            default:
                break;
        }
        try {
            Integer typeNumber = oracleType.getVendorTypeNumber();
            if (typeNumber != null) {
                return JDBCType.valueOf(typeNumber);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null; // 只有 typeNumber 不存在这一种情况，因此吃掉 error。
        }
    }

    public static OracleSqlTypes toOracleType(String dataType) {
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
}
