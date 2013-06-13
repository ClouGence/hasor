/*
 * Copyright 2002-2005 the original author or authors.
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
package org.platform.jdbc;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * {@link PreparedStatement}类型的通用的回调接口。
 * @version : 2013-5-7
 * @author 赵永春 (zyc@byshell.org)
 */
public interface PreparedStatementCallback<T> {
    /***/
    public T doPreparedStatement(PreparedStatement ps) throws SQLException;
}