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
import net.hasor.db.transaction.*;
/**
 *
 * @version : 2015年10月22日
 * @author 赵永春(zyc@hasor.net)
 */
class TransactionTemplateManager implements TransactionTemplate {
    private TransactionManager transactionManager;
    //
    public TransactionTemplateManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    //
    public <T> T execute(TransactionCallback<T> callBack) throws Throwable {
        return this.execute(callBack, Propagation.REQUIRED, Isolation.DEFAULT);
    }
    public <T> T execute(TransactionCallback<T> callBack, Propagation behavior) throws Throwable {
        return this.execute(callBack, behavior, Isolation.DEFAULT);
    }
    public <T> T execute(TransactionCallback<T> callBack, Propagation behavior, Isolation level) throws Throwable {
        TransactionStatus tranStatus = null;
        try {
            tranStatus = transactionManager.getTransaction(behavior, level);
            return callBack.doTransaction(tranStatus);
        } catch (Throwable e) {
            if (tranStatus != null) {
                tranStatus.setRollbackOnly();
            }
            throw e;
        } finally {
            if (tranStatus != null && !tranStatus.isCompleted()) {
                transactionManager.commit(tranStatus);
            }
        }
    }
}