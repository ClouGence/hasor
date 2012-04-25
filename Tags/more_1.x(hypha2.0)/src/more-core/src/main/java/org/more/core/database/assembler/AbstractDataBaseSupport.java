package org.more.core.database.assembler;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.more.core.database.DataBaseSupport;
import org.more.core.database.PagesList;
import org.more.core.database.Query;
import org.more.core.database.QueryCallBack;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
/**
 * 
 * @version : 2011-11-9
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractDataBaseSupport implements DataBaseSupport {
    protected Log      logger     = LogFactory.getLog(this.getClass());
    private DataSource dataSource = null;
    /***/
    public AbstractDataBaseSupport(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    /**获取使用的数据源对象。*/
    public DataSource getDataSource() {
        return this.dataSource;
    }
    /**获取连接对象。*/
    protected Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }
    public void insertForMap(String tableName, Map<String, Object> values) {
        // TODO Auto-generated method stub
    }
    public void updateForMap(String tableName, Map<String, Object> values, Map<String, Object> whereMap) {
        // TODO Auto-generated method stub
    }
    /*-----------------------------------------------------------------------------------XXXX*/
    /**执行查询返回受影响的行数。*/
    public int executeQuery(String queryString) {
        return this.createQuery(queryString).executeQuery();
    };
    /**执行查询语言并且返回这个查询结果.*/
    public List<Map<String, Object>> query(String queryString) {
        return this.createQuery(queryString).query();
    };
    /**执行查询语言并且返回这个查询结果.*/
    public <E> List<E> query(String queryString, Class<E> toType) {
        return this.createQuery(queryString).query(toType);
    };
    /**执行查询，将查询结果进行分页。*/
    public PagesList queryForPages(String queryString, int pageSize) {
        return this.createQuery(queryString).queryForPages(pageSize);
    };
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public Object firstUnique(String queryString) {
        return this.createQuery(queryString).firstUnique();
    };
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public <E> E firstUnique(String queryString, Class<E> toType) {
        return this.createQuery(queryString).firstUnique(toType);
    };
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public Object lastUnique(String queryString) {
        return this.createQuery(queryString).lastUnique();
    };
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public <E> E lastUnique(String queryString, Class<E> toType) {
        return this.createQuery(queryString).lastUnique(toType);
    };
    /*-----------------------------------------------------------------------------------带参数*/
    /**执行查询返回受影响的行数。*/
    public int executeQuery(String queryString, Object... params) {
        return this.createQuery(queryString).executeQuery(params);
    };
    /**执行查询并且返回这个查询结果.*/
    public List<Map<String, Object>> query(String queryString, Object... params) {
        return this.createQuery(queryString).query(params);
    };
    /**执行查询并且返回这个查询结果.*/
    public <E> List<E> query(String queryString, Class<E> toType, Object... params) {
        return this.createQuery(queryString).query(toType, params);
    };
    /**执行查询，将查询结果进行分页。*/
    public PagesList queryForPages(String queryString, int pageSize, Object... params) {
        return this.createQuery(queryString).queryForPages(pageSize, params);
    };
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public Object firstUnique(String queryString, Object... params) {
        return this.createQuery(queryString).firstUnique(params);
    };
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public <E> E firstUnique(String queryString, Class<E> toType, Object... params) {
        return this.createQuery(queryString).firstUnique(toType, params);
    };
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public Object lastUnique(String queryString, Object... params) {
        return this.createQuery(queryString).lastUnique(params);
    };
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public <E> E lastUnique(String queryString, Class<E> toType, Object... params) {
        return this.createQuery(queryString).lastUnique(toType, params);
    };
    /*-----------------------------------------------------------------------------------带回调*/
    /**执行查询返回受影响的行数。*/
    public int executeQuery(String queryString, QueryCallBack callBack, Object... params) {
        return this.createQuery(queryString).executeQuery(callBack, params);
    };
    /**执行查询并且返回这个查询结果.*/
    public List<Map<String, Object>> query(String queryString, QueryCallBack callBack, Object... params) {
        return this.createQuery(queryString).query(callBack, params);
    };
    /**执行查询并且返回这个查询结果.*/
    public <E> List<E> query(String queryString, Class<E> toType, QueryCallBack callBack, Object... params) {
        return this.createQuery(queryString).query(toType, callBack, params);
    };
    /**执行查询，将查询结果进行分页。*/
    public PagesList queryForPages(String queryString, int pageSize, QueryCallBack callBack, Object... params) {
        return this.createQuery(queryString).queryForPages(pageSize, callBack, params);
    };
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public Object firstUnique(String queryString, QueryCallBack callBack, Object... params) {
        return this.createQuery(queryString).firstUnique(callBack, params);
    };
    /**获取到查询结果的第一个对象.如果查询结果为空则返回null.*/
    public <E> E firstUnique(String queryString, Class<E> toType, QueryCallBack callBack, Object... params) {
        return this.createQuery(queryString).firstUnique(toType, callBack, params);
    };
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public Object lastUnique(String queryString, QueryCallBack callBack, Object... params) {
        return this.createQuery(queryString).lastUnique(callBack, params);
    };
    /**获取到查询结果的最后一个对象.如果查询结果为空则返回null.*/
    public <E> E lastUnique(String queryString, Class<E> toType, QueryCallBack callBack, Object... params) {
        return this.createQuery(queryString).lastUnique(toType, callBack, params);
    };
};