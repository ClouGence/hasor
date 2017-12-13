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
/**
 * 事务模版
 * @version : 2015年8月11日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface TransactionCallback<T> {
    /**
     * 执行事务,如需回滚事务,只需要调用 tranStatus 的 setRollbackOnly() 方法即可。
     * 请注意:异常的抛出一会引起事务的回滚。 */
    public T doTransaction(TransactionStatus tranStatus) throws Throwable;
}