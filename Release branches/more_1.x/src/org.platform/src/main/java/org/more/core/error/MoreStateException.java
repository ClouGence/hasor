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
package org.more.core.error;
/**
 * more状态性异常，该类型异常是指在某个时间片段下的异常，该类型异常定义具有名词和动词双重特性，但是该类型异常是受到时间片段限制。
 * 例如：读写状态、重复、多出、缺少、时间片段性的。
 * @version 2009-7-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class MoreStateException extends MoreRuntimeException {
    private static final long serialVersionUID = 5032345759263916241L;
    /**状态性异常*/
    public MoreStateException(String string) {
        super(string);
    }
    /**状态性异常*/
    public MoreStateException(Throwable error) {
        super(error);
    }
    /**状态性异常*/
    public MoreStateException(String string, Throwable error) {
        super(string, error);
    }
}