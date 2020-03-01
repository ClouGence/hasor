package net.example.hasor.config;
import net.hasor.dataql.DimUdfSource;
import net.hasor.dataql.UdfSourceAssembly;
import net.hasor.db.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@DimUdfSource("jdbc")
public class JdbcUdfSource implements UdfSourceAssembly {
    @Inject
    private JdbcTemplate jdbcTemplate;

    /** 单表批量插入 */
    public int batchInsert(String table, List<Map<String, Object>> items) throws SQLException {
        Set<String> keys;
        String str;
        if (CollectionUtils.isEmpty(items)) {
            return 0;
        }
        //
        for (Map<String, Object> it : items) {
            it.put("create_time", new Date((Long) it.get("create_time")));
            it.put("modify_time", new Date((Long) it.get("modify_time")));
        }
        //
        keys = items.get(0).keySet();
        str = "`" + String.join("`,`", keys) + "`";
        StringBuffer stringBuffer = new StringBuffer();
        keys.forEach(key -> stringBuffer.append(" :").append(key).append(" ,"));
        String val = stringBuffer.toString();
        val = val.substring(0, val.length() - 1);
        int[] ints = jdbcTemplate.executeBatch("INSERT INTO " + table + "(" + str + ") VALUES (" + val + ")", items.toArray(new Map[0]));
        return items.size();
    }
}
