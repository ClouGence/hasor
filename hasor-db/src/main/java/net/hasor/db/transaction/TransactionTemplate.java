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
package net.hasor.db.transaction;
import java.sql.SQLException;
/**
 * 事务模版接口
 * @version : 2015年10月22日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface TransactionTemplate {
    /**
     * 开始执行一个事务。
     * @param callBack 调用方法执行事务。
     * @return 返回 {@link TransactionCallback} 接口执行的返回值。
     * @throws SQLException 执行期间发生SQL异常
     */
    public <T> T execute(TransactionCallback<T> callBack) throws Throwable;

    /**
     * 开始执行一个事务。
     * @param callBack 调用方法执行事务。
     * @param behavior 传播属性
     * @return 返回 {@link TransactionCallback} 接口执行的返回值。
     * @throws SQLException 执行期间发生SQL异常
     */
    public <T> T execute(TransactionCallback<T> callBack, Propagation behavior) throws Throwable;

    /**
     * 开始执行一个事务。
     * @param callBack 调用方法执行事务。
     * @param behavior 传播属性
     * @param level 事务隔离级别
     * @return 返回 {@link TransactionCallback} 接口执行的返回值。
     * @throws SQLException 执行期间发生SQL异常
     */
    public <T> T execute(TransactionCallback<T> callBack, Propagation behavior, Isolation level) throws Throwable;
}