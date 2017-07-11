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
package net.hasor.db.transaction.support;
import net.hasor.db.transaction.TranManager;

import javax.sql.DataSource;
/**
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
class SyncTransactionManager extends TranManager {
    public static void setSync(TransactionObject tranConn) {
        currentConnection(tranConn.getDataSource(), tranConn.getHolder());
    }
    public static void clearSync(DataSource dataSource) {
        currentConnection(dataSource, null);
    }
}