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
package net.hasor.dataql.runtime;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.parser.location.RuntimeLocation;

/**
 * DataQL 运行时异常
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-14
 */
public class ThrowRuntimeException extends QueryRuntimeException {
    protected int       throwCode     = 500;
    protected long      executionTime = -1;
    protected DataModel result        = null;

    public ThrowRuntimeException(RuntimeLocation location, String errorMessage) {
        super(location, errorMessage);
    }

    public ThrowRuntimeException(RuntimeLocation location, String errorMessage, Throwable e) {
        super(location, errorMessage, e);
    }

    public ThrowRuntimeException(RuntimeLocation location, String errorMessage, int throwCode, long executionTime, DataModel result) {
        this(location, errorMessage);
        this.throwCode = throwCode;
        this.executionTime = executionTime;
        this.result = result;
    }

    public int getThrowCode() {
        return throwCode;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public DataModel getResult() {
        return result;
    }
}
