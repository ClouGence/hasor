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
package net.hasor.dataway.spi;
import net.hasor.dataql.domain.DomainHelper;
import net.hasor.dataql.runtime.Location;
import net.hasor.dataql.runtime.ThrowRuntimeException;

/**
 * 带有错误Code的异常
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-27
 */
public class StatusMessageException extends ThrowRuntimeException {
    public StatusMessageException(int throwCode, String errorMessage) {
        super(Location.unknownLocation(), errorMessage);
        this.throwCode = throwCode;
        this.result = DomainHelper.convertTo(errorMessage);
    }

    public StatusMessageException(int throwCode, String errorMessage, Throwable e) {
        super(Location.unknownLocation(), errorMessage, e);
        this.throwCode = throwCode;
        this.result = DomainHelper.convertTo(errorMessage);
    }
}