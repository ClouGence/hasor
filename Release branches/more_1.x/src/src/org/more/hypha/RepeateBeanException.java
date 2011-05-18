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
package org.more.hypha;
import org.more.RepeateException;
/**
 * 出现重复定义，出现该异常通常是对以存在的对象或属性进行了第二次重新定义。
 * @version 2009-4-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class RepeateBeanException extends RepeateException {
    /**  */
    private static final long serialVersionUID = 2377606123252842745L;
    /**
     * 出现重复定义，出现该异常通常是对以存在的对象或属性进行了第二次重新定义。
     * @param string 异常的描述信息
     */
    public RepeateBeanException(String string) {
        super(string);
    }
    /**
     * 出现重复定义，出现该异常通常是对以存在的对象或属性进行了第二次重新定义。
     * @param error 异常的描述信息
     */
    public RepeateBeanException(Throwable error) {
        super(error);
    }
    /**
     * 出现重复定义，出现该异常通常是对以存在的对象或属性进行了第二次重新定义。
     * @param string 异常的描述信息。
     * @param error 承接的上一个异常对象。
     */
    public RepeateBeanException(String string, Throwable error) {
        super(string, error);
    }
}