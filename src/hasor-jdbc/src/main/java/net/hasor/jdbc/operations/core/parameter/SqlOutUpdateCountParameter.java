package net.hasor.jdbc.operations.core.parameter;
import java.sql.Types;
/**
 * 这是一个隐含的返回结果，表示从存储过程返回的更新记录总数。
 * @version : 2013-10-14
 * @author 赵永春(zyc@hasor.net)
 */
public class SqlOutUpdateCountParameter extends SqlOutVarParameter {
    /** 创建一个 SqlReturnUpdateCount 类型 SQL 参数对象。*/
    public SqlOutUpdateCountParameter(String name) {
        super(name, Types.INTEGER);
    }
}