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
 * 找不到异常，通常出现该类异常时通常是要请求的目标资源不存在。这种情况可能目标资源已经定义，也可能是未定义。
 * @version 2009-4-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class NotFoundException extends RuntimeException {
    /**  */
    private static final long serialVersionUID = 3664651649094973500L;
    /**
    * 找不到异常，通常出现该类异常时通常是要请求的目标资源不存在。这种情况可能目标资源已经定义，也可能是未定义。
     * @param string 异常的描述信息
     */
    public NotFoundException(String string) {
        super(string);
    }
    /**
    * 找不到异常，通常出现该类异常时通常是要请求的目标资源不存在。这种情况可能目标资源已经定义，也可能是未定义。
     * @param error 异常的描述信息
     */
    public NotFoundException(Throwable error) {
        super(error);
    }
    /**
    * 找不到异常，通常出现该类异常时通常是要请求的目标资源不存在。这种情况可能目标资源已经定义，也可能是未定义。
     * @param string 异常的描述信息。
     * @param error 承接的上一个异常对象。
     */
    public NotFoundException(String string, Throwable error) {
        super(string, error);
    }
}