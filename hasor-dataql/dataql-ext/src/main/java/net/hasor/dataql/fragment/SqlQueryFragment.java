package net.hasor.dataql.fragment;
import net.hasor.dataql.DimFragment;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.Hints;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.IOUtils;

import javax.inject.Inject;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@DimFragment("sql_exec")
public class SqlQueryFragment implements FragmentProcess {
    @Inject
    private JdbcTemplate jdbcTemplate;

    private static enum SqlMode {
        Execute, Procedure, Query
    }

    @Override
    public Object runFragment(Hints hint, Map<String, Object> paramMap, String fragmentString) throws Throwable {
        List<String> readLines = IOUtils.readLines(new StringReader(fragmentString));
        SqlMode sqlMode = null;
        boolean multipleLines = false;
        for (String lineStr : readLines) {
            String tempLine = lineStr.trim();
            if (!multipleLines) {
                // 空行
                if (StringUtils.isBlank(tempLine)) {
                    continue;
                }
                // 单行注释
                if (tempLine.startsWith("--") && tempLine.startsWith("#")) {
                    continue;
                }
                // 多行注释
                if (tempLine.startsWith("/*")) {
                    multipleLines = true;
                }
            }
            if (multipleLines) {
                if (tempLine.contains("*/")) {
                    tempLine = tempLine.substring(tempLine.indexOf("*/")).trim();
                    multipleLines = false;
                } else {
                    continue;
                }
            }
            //
            if (tempLine.startsWith("insert") || tempLine.startsWith("update") || tempLine.startsWith("delete")) {
                sqlMode = SqlMode.Execute;
            } else if (tempLine.startsWith("exec")) {
                sqlMode = SqlMode.Procedure;
            } else {
                sqlMode = SqlMode.Query;
            }
            break;
        }
        if (sqlMode == null) {
            throw new SQLException("Unknown query statement. -> " + fragmentString);
        }
        //
        if (SqlMode.Query == sqlMode) {
            List<Map<String, Object>> mapList = this.jdbcTemplate.queryForList(fragmentString, paramMap);
            if (mapList != null && mapList.size() == 1) {
                Map<String, Object> objectMap = mapList.get(0);
                if (objectMap != null && objectMap.size() == 1) {
                    Set<Map.Entry<String, Object>> entrySet = objectMap.entrySet();
                    Map.Entry<String, Object> objectEntry = entrySet.iterator().next();
                    return objectEntry.getValue();
                }
            }
            return mapList;
        } else if (SqlMode.Execute == sqlMode) {
            return this.jdbcTemplate.executeUpdate(fragmentString, paramMap);
        } else if (SqlMode.Procedure == sqlMode) {
            throw new SQLException("Procedure not support.");
        }
        throw new SQLException("Unknown SqlMode.");//不可能走到这里
    }
}
