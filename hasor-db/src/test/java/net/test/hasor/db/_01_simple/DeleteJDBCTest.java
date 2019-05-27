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
package net.test.hasor.db._01_simple;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.test.hasor.db._02_datasource.warp.SingleDataSourceWarp;
import org.junit.Test;

import java.sql.SQLException;
/***
 * 基本的delete操作语句执行
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class DeleteJDBCTest {
    public void simple_DeleteJDBCTest() throws SQLException {
        System.out.println("--->>simple_DeleteJDBCTest<<--");
        //
        AppContext app = Hasor.create().setMainSettings("jdbc-config.xml").build(new SingleDataSourceWarp());
        JdbcTemplate jdbc = app.getInstance(JdbcTemplate.class);
        //
    }
}