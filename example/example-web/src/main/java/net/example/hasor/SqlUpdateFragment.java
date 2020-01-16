package net.example.hasor;
import net.hasor.dataql.DimFragment;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.Hints;
import net.hasor.db.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import java.util.Map;

@DimFragment("sql_execute")
public class SqlUpdateFragment implements FragmentProcess {
    @Inject
    private JdbcTemplate jdbcTemplate;

    @Override
    public Object runFragment(Hints hint, Map<String, Object> params, String fragmentString) throws Throwable {
        return this.jdbcTemplate.executeUpdate(fragmentString, params);
    }
}