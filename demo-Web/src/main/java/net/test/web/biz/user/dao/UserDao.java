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
import java.sql.SQLException;
import javax.sql.DataSource;
import net.hasor.db.orm.AbstractDao;
import net.hasor.db.orm.ar.dialect.SQLBuilderEnum;
import net.test.web.biz.user.entity.UserBean;
/**
 * Dao层类
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserDao extends AbstractDao<UserBean> {
    public UserDao(DataSource dataSource) {
        super(dataSource, SQLBuilderEnum.MySql);
    }
    public void createUser(UserBean user) throws SQLException {
        this.saveAsNew(user);
    }
}