/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.jdbc.transaction._;
import static net.hasor.jdbc.transaction.TransactionBehavior.PROPAGATION_MANDATORY;
import static net.hasor.jdbc.transaction.TransactionBehavior.PROPAGATION_NESTED;
import static net.hasor.jdbc.transaction.TransactionBehavior.PROPAGATION_NEVER;
import static net.hasor.jdbc.transaction.TransactionBehavior.PROPAGATION_NOT_SUPPORTED;
import static net.hasor.jdbc.transaction.TransactionBehavior.PROPAGATION_REQUIRED;
import static net.hasor.jdbc.transaction.TransactionBehavior.RROPAGATION_REQUIRES_NEW;
import java.sql.SQLException;
import net.hasor.Hasor;
import net.hasor.jdbc.IllegalTransactionStateException;
import net.hasor.jdbc.TransactionDataAccessException;
import net.hasor.jdbc.transaction.TransactionBehavior;
import net.hasor.jdbc.transaction.TransactionStatus;
import net.hasor.jdbc.transaction.core.AbstractPlatformTransactionManager;
import net.hasor.jdbc.transaction.core.AbstractTransactionStatus;
/**
 * 某一个数据源的事务管理器
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
public class DataSourceTransactionManager extends AbstractPlatformTransactionManager {
    protected Object doGetTransaction() {
        // TODO Auto-generated method stub
        return null;
    }
    protected boolean isExistingTransaction(Object transaction) {
        // TODO Auto-generated method stub
        return false;
    }
    protected void doBegin(Object transaction, AbstractTransactionStatus status) throws SQLException {
        // TODO Auto-generated method stub
    }
    protected void doCommit(Object transaction, AbstractTransactionStatus status) throws SQLException {
        // TODO Auto-generated method stub
    }
    protected void doRollback(Object transaction, AbstractTransactionStatus status) throws SQLException {
        // TODO Auto-generated method stub
    }
}