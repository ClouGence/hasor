package net.example.hasor;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.dataql.DimUdfSource;
import net.hasor.dataql.Finder;
import net.hasor.dataql.Udf;
import net.hasor.dataql.UdfSource;
import net.hasor.dataql.sdk.TypeUdfMap;
import net.hasor.db.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

@DimUdfSource("jdbc")
public class JdbcUdfSource implements UdfSource {
    @Inject
    private JdbcTemplate jdbcTemplate;

    @Override
    public Supplier<Map<String, Udf>> getUdfResource(Finder finder) {
        Supplier<?> supplier = () -> finder.findBean(getClass());
        Predicate<Method> predicate = method -> true;
        return InstanceProvider.of(new TypeUdfMap(getClass(), supplier, predicate));
    }

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