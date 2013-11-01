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
package net.hasor.jdbc.transaction;
/**
 * 表示一个事务状态
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
public interface TransactionStatus {
    /**获取事务使用的传播行为*/
    public TransactionBehavior getTransactionBehavior();
    /**获取事务的隔离级别*/
    public TransactionLevel getIsolationLevel();
    //
    /**事务是否已经完成。
     * <p>当事务已经递交或者被回滚就标志着已完成。*/
    public boolean isCompleted();
    /**设置事务状态为回滚，作为替代抛出异常进而触发回滚操作。
     * 事务管理器将会处置事务回滚。*/
    public void setRollbackOnly();
    /**返回事务是否已被标记为回滚。*/
    public boolean isRollbackOnly();
    /**表示事务是否携带了一个保存点，嵌套事务通常会创建一个保存点作为嵌套事务与上一层事务的分界点。
     * <p>注意：如果事务中包含保存点，则在递交事务时只处理这个保存点。*/
    public boolean hasSavepoint();
    //    /**是否为只读模式。*/
    //    public boolean isReadOnly();
    /**是否使用了一个全新的数据库连接开启事务*/
    public boolean isNewConnection();
}