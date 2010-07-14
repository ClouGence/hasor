/*
 * Copyright 2008-2009 the original author or authors.
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
package org.console;
import java.sql.SQLException;
import org.more.dao.jdbc.JobSupport;
import org.more.submit.ActionStack;
/**/
public class JDBCAction extends JobSupport {
    /**插入数据。*/
    public boolean insert(ActionStack stack) throws SQLException {
        this.getJdbcDaoSupport().execute("insert into r values(1,'a');");
        this.getJdbcDaoSupport().execute("insert into r values(2,'a');");
        return true;
    }
    /**更新数据。*/
    public boolean update(ActionStack stack) throws SQLException {
        this.getJdbcDaoSupport().execute("update r set id=10 , name='b' where id=1;");
        this.getJdbcDaoSupport().execute("update r set id=20 , name='b' where id=2;");
        return true;
    }
    /***/
    public void all(ActionStack stack) throws SQLException {
        this.insert(stack);
        this.update(stack);
    }
}