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
package org.more.core.error;
/**
 * more动作性异常，该类型异常是指某个过程中发生异常。通常异常定义是动词性质的。
 * 例如：转换、初始化、执行、检查、时间性的。
 * @version 2009-10-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class MoreActionException extends MoreException {
    private static final long serialVersionUID = 4235042411000290872L;
    /** more动作类异常。*/
    public MoreActionException(String string) {
        super(string);
    }
    /** more动作类异常。*/
    public MoreActionException(Throwable error) {
        super(error);
    }
    /** more动作类异常。*/
    public MoreActionException(String string, Throwable error) {
        super(string, error);
    }
}