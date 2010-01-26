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
package org.more.core.json;
import org.more.FormatException;
/**
 * 解析json数据错误，通常是当解析json数据或者将对象序列化为json数据引发的该类异常。
 * @version 2010-1-7
 * @author 赵永春 (zyc@byshell.org)
 */
public class JsonException extends FormatException {
    /**  */
    private static final long serialVersionUID = 6079652246835019946L;
    /**
     * 解析json数据错误，通常是当解析json数据或者将对象序列化为json数据引发的该类异常。
     * @param string 异常的描述信息
     */
    public JsonException(String string) {
        super(string);
    }
    /**
     * 解析json数据错误，通常是当解析json数据或者将对象序列化为json数据引发的该类异常。
     * @param error 异常的描述信息
     */
    public JsonException(Throwable error) {
        super(error);
    }
    /**
     * 解析json数据错误，通常是当解析json数据或者将对象序列化为json数据引发的该类异常。
     * @param string 异常的描述信息。
     * @param error 承接的上一个异常对象。
     */
    public JsonException(String string, Throwable error) {
        super(string, error);
    }
}