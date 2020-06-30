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
package net.hasor.dataql.fx.db;
import javax.sql.DataSource;
import java.util.EventListener;

/**
 * 当 DataQL 执行过程中用来获取对应的动态数据源，指定数据源的名字需要通过 HINT：FRAGMENT_SQL_DATA_SOURCE
 *
 * 该 SPI 允许应用程序在任意时候更换某个名字的数据库连接。
 * ps ：只有当初始化没有注册的数据源才会利用 LookupDataSourceListener 进行查找发现。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-03
 */
public interface LookupDataSourceListener extends EventListener {
    /**
     * 当 DataQL 执行过程中用来获取对应的动态数据源。
     * @param lookupName 要查找的数据源
     * @return 返回最终需要的数据源。
     */
    public DataSource lookUp(String lookupName);
}