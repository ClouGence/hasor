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
 * 这个接口用来映射 JDBC 结果集中一行数据。
 * @version : 2013-10-9
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @author 赵永春(zyc@hasor.net)
 */
public interface RowMapper<T> {
    /**
     * 实现这个方法为结果集的一行记录进行转换，并将最终转换结果返回。如果返回为 null 等同于忽略该行。
     * 需要注意，不要调用结果集的 next() 方法。
     * @param rs 记录集
     * @param rowNum 当前记录的行号
     */
    public T mapRow(ResultSet rs, int rowNum) throws SQLException;
}