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
 * 企图访问只读资源异常，当企图在某资源在只读期间对其进行设置值时引发。
 * @version 2009-4-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class ReadOnlyException extends RuntimeException {
    /**  */
    private static final long serialVersionUID = 6090587253990681159L;
    /**
     * 企图访问只读资源异常，当企图在某资源在只读期间对其进行设置值时引发。
     * @param string 异常的描述信息
     */
    public ReadOnlyException(String string) {
        super(string);
    }
    /**
     * 企图访问只读资源异常，当企图在某资源在只读期间对其进行设置值时引发。
     * @param error 异常的描述信息
     */
    public ReadOnlyException(Throwable error) {
        super(error);
    }
    /**
     * 企图访问只读资源异常，当企图在某资源在只读期间对其进行设置值时引发。
     * @param string 异常的描述信息。
     * @param error 承接的上一个异常对象。
     */
    public ReadOnlyException(String string, Throwable error) {
        super(string, error);
    }
}