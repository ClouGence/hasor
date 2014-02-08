/*
 * Copyright 2002-2006 the original author or authors.
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
package net.hasor.jdbc.exceptions;
import org.more.util.exception.NestableRuntimeException;
/**
 * JDBC 异常根
 * @version : 2013-10-12
 * @author 赵永春(zyc@hasor.net)
 */
public class DataAccessException extends NestableRuntimeException {
    private static final long serialVersionUID = -476436113710224961L;
    /**JDBC 异常根*/
    public DataAccessException(String msg) {
        super(msg);
    }
    /**JDBC 异常根*/
    public DataAccessException(Throwable cause) {
        super(cause);
    }
    /**JDBC 异常根*/
    public DataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}