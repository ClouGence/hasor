package net.hasor.jdbc.jdbc.parameter;
import java.sql.Types;
/**
 * 从存储过程返回的更新记录总数，该参数是传出参数。
 * @version : 2013-10-14
 * @author 赵永春(zyc@hasor.net)
 */
public class SqlUpdateCountParameter extends SqlInOutParameter {
    /** 创建一个 SqlReturnUpdateCount 类型 SQL 参数对象。*/
    public SqlUpdateCountParameter(String name) {
        super(name, Types.INTEGER);
    }
}