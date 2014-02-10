/*
 * Copyright 2002-2008 the original author or authors.
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
package net.hasor.jdbc.template.exceptions;
/**
 * 无效的数据异常
 * @version : 2013-10-12
 * @author 赵永春(zyc@hasor.net)
 */
public class InvalidDataAccessException extends DataAccessException {
    private static final long serialVersionUID = -6108785691952536352L;
    private String            sql;
    /**无效的数据异常*/
    public InvalidDataAccessException(String task, String sql, Throwable ex) {
        super(task + "; invalid ResultSet access for SQL [" + sql + "]", ex);
        this.sql = sql;
    }
    /**无效的数据异常*/
    public InvalidDataAccessException(String task, Throwable ex) {
        super(task, ex);
    }
    /**无效的数据异常*/
    public InvalidDataAccessException(String task) {
        super(task);
    }
    /**无效的数据异常*/
    public InvalidDataAccessException(Throwable ex) {
        super(ex.getMessage(), ex);
    }
    /** Return the SQL that caused the problem.*/
    public String getSql() {
        return this.sql;
    }
}