package webapp.dso;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DsHelper {
    public static void initData(DataSource ds) {
        Connection conn = null;
        try {
            conn = ds.getConnection();
            String[] sqls = getSqlFromFile();
            for (String sql : sqls) {
                runSql(conn, sql);
            }

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException sqlException) {
                //ignore
            }
        }
    }

    private static String[] getSqlFromFile() {
        try {
            InputStream ins = DsHelper.class.getResourceAsStream("/db/schema.sql");
            int len = ins.available();
            byte[] bs = new byte[len];
            ins.read(bs);
            String str = new String(bs, "UTF-8");
            String[] sql = str.split(";");
            return sql;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void runSql(Connection conn, String sql) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeUpdate();
        ps.close();
    }
}
