package net.hasor.dataql.fx.db;
import net.hasor.dataql.Hints;
import net.hasor.dataql.Udf;
import net.hasor.dataql.UdfSourceAssembly;
import net.hasor.dataql.fx.db.fragment.SqlQueryFragment;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionTemplate;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;

@Singleton
public class JdbcOperation implements UdfSourceAssembly {
    @Inject
    private TransactionTemplate transactionTemplate;
    @Inject
    private JdbcTemplate        jdbcTemplate;
    @Inject
    private SqlQueryFragment    queryFragment;

    /** 提供一个 Udf Callback，用来为 DataQL 中的 lambda 查询提供事务能力 */
    public Object jdbcTran(final Hints hints, final Udf udf, final Propagation propagation) throws Throwable {
        return this.transactionTemplate.execute(tranStatus -> {
            return udf.call(hints);
        }, propagation);
    }

    /** 接受外部 SQL 字符串，并在事务模版中运行 */
    public Object execSQL(final Hints hints, String sqlString, final Propagation propagation) throws Throwable {
        return this.transactionTemplate.execute(tranStatus -> {
            return queryFragment.runFragment(hints, Collections.emptyMap(), sqlString);
        }, propagation);
    }
}