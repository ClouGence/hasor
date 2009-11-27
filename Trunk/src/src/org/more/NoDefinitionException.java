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
 * 未定义异常，通常出现该类异常是由于使用了未定义的属性或者策略，同时该异常也表示那些试图访问不存在的资源异常。
 * Date : 2009-4-29
 * @author 赵永春
 */
public class NoDefinitionException extends RuntimeException {
    /**  */
    private static final long serialVersionUID = 3664651649094973500L;
    /**
     * 未定义异常，通常出现该类异常是由于使用了未定义的属性或者策略，同时该异常也表示那些试图访问不存在的资源异常。
     * @param string 异常的描述信息
     */
    public NoDefinitionException(String string) {
        super(string);
    }
    /**
     * 未定义异常，通常出现该类异常是由于使用了未定义的属性或者策略，同时该异常也表示那些试图访问不存在的资源异常。
     * @param error 异常的描述信息
     */
    public NoDefinitionException(Throwable error) {
        super(error);
    }
    /**
     * 未定义异常，通常出现该类异常是由于使用了未定义的属性或者策略，同时该异常也表示那些试图访问不存在的资源异常。
     * @param string 异常的描述信息。
     * @param error 承接的上一个异常对象。
     */
    public NoDefinitionException(String string, Throwable error) {
        super(string, error);
    }
}
