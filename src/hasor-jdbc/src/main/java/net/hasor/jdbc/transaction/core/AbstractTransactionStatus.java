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
package net.hasor.jdbc.transaction.core;
import net.hasor.jdbc.TransactionDataAccessException;
import net.hasor.jdbc.transaction.SavepointManager;
import net.hasor.jdbc.transaction.TransactionBehavior;
import net.hasor.jdbc.transaction.TransactionStatus;
/**
 * 表示一个用于管理事务的状态点
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractTransactionStatus implements TransactionStatus {
    
    protected abstract SavepointManager getSavepointManager() {}
    /**设定一个数据库事务保存点。*/
    public void markHeldSavepoint() {
        // TODO Auto-generated method stub
    }
    /***/
    public void releaseHeldSavepoint() {
        // TODO Auto-generated method stub
    }
    public void rollbackToHeldSavepoint() {
        // TODO Auto-generated method stub
    }
    public void setSuspendHolder(Object suspendHolder) {
        // TODO Auto-generated method stub
    }
    public Object getSuspendedTransactionHolder() {
        return null;
    }
    public void setCompleted() {
        // TODO Auto-generated method stub
    }
    public Object createSavepoint() throws TransactionDataAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public void rollbackToSavepoint(Object savepoint) throws TransactionDataAccessException {
        // TODO Auto-generated method stub
    }
    public void releaseSavepoint(Object savepoint) throws TransactionDataAccessException {
        // TODO Auto-generated method stub
    }
    public TransactionBehavior getTransactionBehavior() {
        // TODO Auto-generated method stub
        return null;
    }
    public boolean isCompleted() {
        // TODO Auto-generated method stub
        return false;
    }
    public void setRollbackOnly() {
        // TODO Auto-generated method stub
    }
    public boolean isRollbackOnly() {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean hasSavepoint() {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean isReadOnly() {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean isNew() {
        // TODO Auto-generated method stub
        return false;
    }
}