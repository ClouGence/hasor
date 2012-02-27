package org.more.core.database.assembler.jdbc.sqlserver;
import javax.sql.DataSource;
import org.more.core.database.assembler.AbstractDataBaseSupport;
/**
 * SQL Server的默认实现。
 * @version : 2011-11-9
 * @author 赵永春 (zyc@byshell.org)
 */
public class SQLDataBaseSupport extends AbstractDataBaseSupport {
    public SQLDataBaseSupport(DataSource dataSource) {
        super(dataSource);
    }
    /**根据sql语句创建一个查询接口对象.利用该对象可以进行复杂查询.*/
    public SQLQuery createQuery(String sql) {
        return new SQLQuery(sql, this);
    };
};