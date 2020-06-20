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
package net.hasor.dataql.runtime.mem;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.runtime.InstructRuntimeException;
import net.hasor.dataql.runtime.Location.RuntimeLocation;

/**
 * 代理 Lambda 使其成为 UDF.
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class RefLambdaCallException extends InstructRuntimeException {
    private final int       resultCode;
    private final DataModel result;

    public RefLambdaCallException(RuntimeLocation location, int resultCode, DataModel result) {
        super(location, "udf or lambda failed.");
        this.resultCode = resultCode;
        this.result = result;
    }

    public int getResultCode() {
        return resultCode;
    }

    public DataModel getResult() {
        return result;
    }
}