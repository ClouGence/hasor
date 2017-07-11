/*
 * Copyright 2002-2007 the original author or authors.
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
package net.hasor.db.jdbc;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * JDBC 结果集行数据处理器。
 * @version : 2013-10-9
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @author 赵永春(zyc@hasor.net)
 */
public interface RowCallbackHandler {
    /**实现这个方法用于处理结果集的一行记录。
     * 注意：不要调用结果集的 next() 方法。*/
    public void processRow(ResultSet rs) throws SQLException;
}