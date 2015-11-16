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
import java.sql.Connection;
import java.sql.SQLException;
/**
 * 连接管理器
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
public interface ConnectionManager {
    /**增加引用计数,一个因为持有人已被请求。*/
    public void requested();
    /**减少引用计数,一个因为持有人已被释放。 */
    public void released() throws SQLException;
    /**获取数据库连接。*/
    public Connection getConnection() throws SQLException;
    /**则表示当前数据库连接是否被打开，被打开的连接一定有引用。*/
    public boolean isOpen();
}