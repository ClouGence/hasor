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
package org.more.submit.impl;
import org.more.submit.Result;
/**
 * 结果处理标记对象。
 * @version : 2011-7-27
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultResultImpl<T> implements Result<T> {
    private String name        = null;
    private T      returnValue = null;
    //
    public DefaultResultImpl(String name) {
        this.name = name;
    };
    public DefaultResultImpl(String name, T returnValue) {
        this.name = name;
        this.returnValue = returnValue;
    };
    public T getReturnValue() {
        return this.returnValue;
    };
    public void setReturnValue(T returnValue) {
        this.returnValue = returnValue;
    }
    public String getName() {
        return this.name;
    };
}