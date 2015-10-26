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
package net.test.hasor.db._06_transaction.simple;
import net.hasor.core.Inject;
import net.hasor.db.transaction.TransactionCallback;
import net.hasor.db.transaction.TransactionStatus;
import net.hasor.db.transaction.TransactionTemplate;
/**
 * 
 * @version : 2015年10月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServiceBean {
    @Inject
    private TransactionTemplate transactionTemplate;
    public void aa() throws Throwable {
        this.transactionTemplate.execute(new TransactionCallback<Void>() {
            public Void doTransaction(TransactionStatus tranStatus) throws Throwable {
                // TODO Auto-generated method stub
                return null;
            }
        });
    }
}