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
import net.hasor.dataql.runtime.Location.RuntimeLocation;

/**
 * DataQL 运行时异常
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-14
 */
public class InstructRuntimeException extends RuntimeException {
    private RuntimeLocation location = null;

    public InstructRuntimeException(RuntimeLocation location, String errorMessage) {
        super("[" + location.toErrorMessage() + "] " + errorMessage);
        this.location = location;
    }

    public InstructRuntimeException(RuntimeLocation location, String errorMessage, Throwable e) {
        super("[" + location.toErrorMessage() + "] " + errorMessage, e);
        this.location = location;
    }

    public InstructRuntimeException(RuntimeLocation location, Throwable e) {
        super("[" + location.toErrorMessage() + "] " + e.getMessage(), e);
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public int getProgramAddress() {
        return this.location.getProgramAddress();
    }

    public int getMethodAddress() {
        return this.location.getMethodAddress();
    }

    public int getLineNumber() {
        return this.location.getLineNumber();
    }

    public int getColumnNumber() {
        return this.location.getColumnNumber();
    }
}