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
package org.more;
/**
 * 初始化异常，出现该异常通常是在对某些对象执行初始化或者要求某些对象或者环境进行初始化时发生异常。
 * Date : 2009-7-7
 * @author 赵永春
 */
public class InitializationException extends RuntimeException {
    /**  */
    private static final long serialVersionUID = 6489409968925378968L;
    /**
     * 初始化异常，出现该异常通常是在对某些对象执行初始化或者要求某些对象或者环境进行初始化时发生异常。
     * @param string 异常的描述信息
     */
    public InitializationException(String string) {
        super(string);
    }
    /**
     * 初始化异常，出现该异常通常是在对某些对象执行初始化或者要求某些对象或者环境进行初始化时发生异常。
     * @param error 异常的描述信息
     */
    public InitializationException(Throwable error) {
        super(error);
    }
    /**
     * 初始化异常，出现该异常通常是在对某些对象执行初始化或者要求某些对象或者环境进行初始化时发生异常。
     * @param string 异常的描述信息。
     * @param error 承接的上一个异常对象。
     */
    public InitializationException(String string, Throwable error) {
        super(string, error);
    }
}
