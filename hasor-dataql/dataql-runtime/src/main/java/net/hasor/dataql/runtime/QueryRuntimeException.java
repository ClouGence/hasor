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
import net.hasor.dataql.DataQueryException;
import net.hasor.dataql.parser.location.RuntimeLocation;

/**
 * DataQL 运行时异常
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-14
 */
public class QueryRuntimeException extends DataQueryException {
    public QueryRuntimeException(RuntimeLocation location, String errorMessage) {
        super(location, errorMessage);
    }

    public QueryRuntimeException(RuntimeLocation location, String errorMessage, Throwable e) {
        super(location, errorMessage, e);
    }

    public QueryRuntimeException(RuntimeLocation location, Throwable e) {
        super(location, e);
    }

    public int getProgramAddress() {
        return ((RuntimeLocation) this.location).getProgramAddress();
    }

    public int getMethodAddress() {
        return ((RuntimeLocation) this.location).getMethodAddress();
    }
}
