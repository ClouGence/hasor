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
/**/
public class JobJDBC extends JobSupport {
    /**≤Â»Î ˝æ›°£*/
    public boolean insert(int id, String name) throws SQLException {
        this.getJdbcDaoSupport().execute("insert into r values(" + id + ",'" + name + "');");
        this.getJdbcDaoSupport().execute("insert into r values(" + id + ",'" + name + "');");
        return true;
    }
}