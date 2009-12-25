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
 * 不支持的异常。引发该异常的原因通常是执行了某些方法，但是这些方法不被支持。或者一些操作不被支持。
 * Date : 2009-7-7
 * @author 赵永春
 */
public class DoesSupportException extends RuntimeException {
    /**  */
    private static final long serialVersionUID = -8273073048231674543L;
    /**
     * 不支持的异常。引发该异常的原因通常是执行了某些方法，但是这些方法不被支持。或者一些操作不被支持。
     * @param string 异常的描述信息
     */
    public DoesSupportException(String string) {
        super(string);
    }
    /**
     * 不支持的异常。引发该异常的原因通常是执行了某些方法，但是这些方法不被支持。或者一些操作不被支持。
     * @param error 异常的描述信息
     */
    public DoesSupportException(Throwable error) {
        super(error);
    }
    /**
     * 不支持的异常。引发该异常的原因通常是执行了某些方法，但是这些方法不被支持。或者一些操作不被支持。
     * @param string 异常的描述信息。
     * @param error 承接的上一个异常对象。
     */
    public DoesSupportException(String string, Throwable error) {
        super(string, error);
    }
}
