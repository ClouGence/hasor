/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.db.ar;
import java.sql.SQLException;
import java.util.Map;
import net.hasor.db.ar.record.DataBase;
import net.hasor.db.ar.record.Record;
import net.hasor.db.jdbc.JdbcOperations;
import org.more.util.ClassUtils;
/**
 * 基础Dao
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class AbstractDao<ENT> {
    protected Class<ENT> getRecordType() {
        return (Class<ENT>) ClassUtils.getSuperClassGenricType(this.getClass(), 0);
    };
    protected Record newRecord(ENT record) {
        // TODO Auto-generated method stub
        return null;
    };
    protected JdbcOperations getJdbc() {
        return getDataBase().getJdbc();
    };
    private DataBase getDataBase() {
        // TODO Auto-generated method stub
        return null;
    };
    //
    /**保存为新增。*/
    protected boolean saveAsNew(ENT record) throws SQLException {
        return this.getDataBase().saveAsNew(newRecord(record));
    }
    /**根据ID，保存或更新。*/
    protected boolean saveOrUpdate(ENT record) throws SQLException {
        return this.getDataBase().saveOrUpdate(newRecord(record));
    }
    /**根据ID，删除。*/
    protected int delete(ENT record) throws SQLException {
        return this.getDataBase().delete(newRecord(record));
    }
    /**根据ID，更新。*/
    protected int update(ENT record) throws SQLException {
        return this.getDataBase().update(newRecord(record));
    }
    /**根据样本执行删除。*/
    protected int deleteByExample(ENT example) throws SQLException {
        return this.getDataBase().deleteByExample(newRecord(example));
    }
    /**根据样本执行更新。*/
    protected int updateByExample(ENT example, ENT record) throws SQLException {
        return this.getDataBase().updateByExample(newRecord(example), newRecord(record));
    }
    //
    /**从数据库中查询满足该对象特征的。*/
    protected PageResult<ENT> listByExample(ENT example) throws SQLException {
        return this.getDataBase().listByExample(getRecordType(), newRecord(example));
    }
    /**从数据库中查询满足该对象特征的。*/
    protected PageResult<ENT> listByExample(ENT example, Paginator pageInfo) throws SQLException {
        return this.getDataBase().listByExample(getRecordType(), newRecord(example), pageInfo);
    }
    //
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    protected PageResult<ENT> queryBySQL(String sqlQuery) throws SQLException {
        return this.getDataBase().queryBySQL(getRecordType(), sqlQuery);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    protected PageResult<ENT> queryBySQL(String sqlQuery, Object... params) throws SQLException {
        return this.getDataBase().queryBySQL(getRecordType(), sqlQuery, params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    protected PageResult<ENT> queryBySQL(String sqlQuery, Map<String, Object> params) throws SQLException {
        return this.getDataBase().queryBySQL(getRecordType(), sqlQuery, params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    protected PageResult<ENT> queryBySQL(String sqlQuery, Paginator paginator) throws SQLException {
        return this.getDataBase().queryBySQL(getRecordType(), sqlQuery, paginator);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    protected PageResult<ENT> queryBySQL(String sqlQuery, Paginator paginator, Object... params) throws SQLException {
        return this.getDataBase().queryBySQL(getRecordType(), sqlQuery, paginator, params);
    }
    /**根据SQL语句执行查询返回{@link PageResult}。*/
    protected PageResult<ENT> queryBySQL(String sqlQuery, Paginator paginator, Map<String, Object> params) throws SQLException {
        return this.getDataBase().queryBySQL(getRecordType(), sqlQuery, paginator, params);
    }
}