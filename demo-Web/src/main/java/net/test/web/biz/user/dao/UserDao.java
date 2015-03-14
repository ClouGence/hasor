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
package net.test.web.biz.user.dao;
import javax.sql.DataSource;
import net.hasor.core.AppContext;
import net.hasor.core.InjectMembers;
import net.hasor.db.orm.AbstractDao;
import net.hasor.db.orm.PageResult;
import net.hasor.db.orm.Paginator;
import net.hasor.db.orm.ar.dialect.SQLBuilderEnum;
import net.test.web.biz.user.entity.UserBean;
import org.more.bizcommon.ResultDO;
/**
 * Dao
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserDao extends AbstractDao<UserBean> implements InjectMembers {
    public void doInject(AppContext appContext) {
        DataSource dataSource = appContext.findBindingBean("default", DataSource.class);
        this.setDataSource(dataSource);
        this.setDialect(SQLBuilderEnum.HSQL);
    }
    //
    /*增*/
    public ResultDO<Boolean> createUser(UserBean user) {
        try {
            boolean res = this.saveAsNew(user);
            return new ResultDO<Boolean>(true).setResult(res);
        } catch (Exception e) {
            return new ResultDO<Boolean>(false).setThrowable(e);
        }
    }
    //
    /*删*/
    public ResultDO<Boolean> deleteUser(UserBean user) {
        try {
            int res = this.delete(user);
            return new ResultDO<Boolean>(true).setResult(res != 0);
        } catch (Exception e) {
            return new ResultDO<Boolean>(false).setThrowable(e);
        }
    }
    //
    /*改*/
    public ResultDO<Boolean> updateUser(UserBean user) {
        try {
            int res = this.update(user);
            return new ResultDO<Boolean>(true).setResult(res != 0);
        } catch (Exception e) {
            return new ResultDO<Boolean>(false).setThrowable(e);
        }
    }
    //
    /*查*/
    public PageResult<UserBean> userList(Paginator page) {
        try {
            return this.listByExample(new UserBean(), page);
        } catch (Exception e) {
            return new PageResult<UserBean>(page).setThrowable(e);
        }
    }
}