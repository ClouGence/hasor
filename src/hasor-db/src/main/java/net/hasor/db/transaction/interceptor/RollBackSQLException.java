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
package net.hasor.db.transaction.interceptor;
import java.sql.SQLException;
/**
 * 回滚，被拦截方法抛出该异常只会让事务回滚，异常并不会继续抛出。
 * @version : 2014年7月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class RollBackSQLException extends SQLException {
    private static final long serialVersionUID = 1606091559082928855L;
}