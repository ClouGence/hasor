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
 * 属性调用异常，或者执行属性操作时发生异常。
 * @version 2009-6-26
 * @author 赵永春 (zyc@byshell.org)
 */
public class PropertyException extends RuntimeException {
    /**  */
    private static final long serialVersionUID = -7774988512856603877L;
    /**
     * 属性调用异常，或者执行属性操作时发生异常。
     * @param string 异常的描述信息
     */
    public PropertyException(String string) {
        super(string);
    }
    /**
     * 属性调用异常，或者执行属性操作时发生异常。
     * @param error 异常的描述信息
     */
    public PropertyException(Throwable error) {
        super(error);
    }
    /**
     * 属性调用异常，或者执行属性操作时发生异常。
     * @param string 异常的描述信息。
     * @param error 承接的上一个异常对象。
     */
    public PropertyException(String string, Throwable error) {
        super(string, error);
    }
}