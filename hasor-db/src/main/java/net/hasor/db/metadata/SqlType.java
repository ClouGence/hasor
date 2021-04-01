package net.hasor.db.metadata;
import java.sql.JDBCType;

public interface SqlType {
    public String getCodeKey();

    public JDBCType getJdbcType();
}
