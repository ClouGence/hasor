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
package net.hasor.db.datasource;
import java.sql.SQLException;
import java.sql.Savepoint;
/**
 *
 * @version : 2013-10-30
 * @author 赵永春 (zyc@hasor.net)
 */
public interface SavepointManager {
    /**是否支持保存点 */
    public boolean supportSavepoint() throws SQLException;

    /**创建事务的保存点，通过<code>releaseSavepoint</code>方法释放这个保存点。SQLException */
    public Savepoint createSavepoint() throws SQLException;

    /**回滚事务到一个指定的保存点。*/
    public void rollbackToSavepoint(Savepoint savepoint) throws SQLException;

    /**释放某个事务的保存点*/
    public void releaseSavepoint(Savepoint savepoint) throws SQLException;
}