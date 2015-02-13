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
package net.hasor.db.orm.ar;
import net.hasor.db.jdbc.JdbcOperations;
/**
 * 
 * @version : 2014年10月27日
 * @author 赵永春(zyc@hasor.net)
 */
public interface Identify {
    /**
     * ID生成器
     * @param record 需要新ID的记录。
     * @param sechma 记录所用的Sechma
     * @param jdbcOperations 数据库操作接口。
     */
    public Object newUniqueID(Record record, Sechma sechma, JdbcOperations jdbcOperations);
}