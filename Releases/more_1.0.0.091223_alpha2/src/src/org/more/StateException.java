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
 * 转换异常，当发生类型转换，数据转换或者任何转换操作时发生问题则抛出该异常。
 * Date : 2009-7-8
 * @author 赵永春
 */
public class StateException extends Exception {
    /**  */
    private static final long serialVersionUID = 5032345759263916241L;
    /**
     * 转换异常，当发生类型转换，数据转换或者任何转换操作时发生问题则抛出该异常。
     * @param string 异常的描述信息
     */
    public StateException(String string) {
        super(string);
    }
    /**
     * 转换异常，当发生类型转换，数据转换或者任何转换操作时发生问题则抛出该异常。
     * @param error 异常的描述信息
     */
    public StateException(Throwable error) {
        super(error);
    }
    /**
     * 转换异常，当发生类型转换，数据转换或者任何转换操作时发生问题则抛出该异常。
     * @param string 异常的描述信息。
     * @param error 承接的上一个异常对象。
     */
    public StateException(String string, Throwable error) {
        super(string, error);
    }
}
