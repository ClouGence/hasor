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
package org.more.beans.core.injection;
import org.more.InvokeException;
/**
 * 当执行ioc注入时发生异常。
 * @version 2009-11-9
 * @author 赵永春 (zyc@byshell.org)
 */
public class InjectionException extends InvokeException {
    /**  */
    private static final long serialVersionUID = 8083050599002019837L;
    /**
     * 当执行ioc注入时发生异常。
     * @param string 异常的描述信息
     */
    public InjectionException(String string) {
        super(string);
    }
}