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
 * 类格式错误，出现该异常通常是在操作某些数据时该数据格式异常或者不被支持。
 * @version 2011-3-7
 * @author 赵永春 (zyc@byshell.org)
 */
public class ClassFormatException extends FormatException {
    /**  */
    private static final long serialVersionUID = 7621774696047368322L;
    /**
     * 类格式错误，出现该异常通常是在操作某些数据时该数据格式异常或者不被支持。
     * @param string 异常的描述信息
     */
    public ClassFormatException(String string) {
        super(string);
    };
    /**
     * 类格式错误，出现该异常通常是在操作某些数据时该数据格式异常或者不被支持。
     * @param error 异常的描述信息
     */
    public ClassFormatException(Throwable error) {
        super(error);
    };
    /**
     * 类格式错误，出现该异常通常是在操作某些数据时该数据格式异常或者不被支持。
     * @param string 异常的描述信息。
     * @param error 承接的上一个异常对象。
     */
    public ClassFormatException(String string, Throwable error) {
        super(string, error);
    };
};