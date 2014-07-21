/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.classcode;
/**
 * 格式错误，出现该异常通常是在操作某些数据时该数据格式异常或者不被支持。
 * @version 2009-10-17
 * @author 赵永春 (zyc@hasor.net)
 */
public class FormatException extends ClassCodeRuntimeException {
    private static final long serialVersionUID = 7219173260772984152L;
    /**格式异常*/
    public FormatException(final String string) {
        super(string);
    }
    /**格式异常*/
    public FormatException(final Throwable error) {
        super(error);
    }
    /**格式异常*/
    public FormatException(final String string, final Throwable error) {
        super(string, error);
    }
}