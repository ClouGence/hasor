/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.lambda;
import java.sql.SQLException;

/**
 * lambda Delete 执行器
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public interface DeleteExecute<T> extends BoundSqlBuilder {
    /** 根据 Lambda 构造器的条件执行删除 */
    public int doDelete() throws SQLException;

    /** 允许空 Where条件（注意：空 Where 条件会导致删除整个数据库） */
    public DeleteExecute<T> allowEmptyWhere();
}
