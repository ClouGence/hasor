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
package org.more.submit;
import org.more.FormatException;
/**
 * submit的工具类。
 * @version : 2010-7-27
 * @author 赵永春(zyc@byshell.org)
 */
public class Util {
    /**格式化调用字符串，如果调用字符串没有指定要调用的目标方法则格式化之后将使用execute作为默认方法。*/
    public static String formatInvokeString(String invokeString) {
        String[] is = invokeString.split("\\.");
        String actionName = null;
        String actionMethod = null;
        if (is.length > 2)
            throw new FormatException("错误的invokeString字符串格式");
        if (is.length >= 1)
            actionName = is[0];
        if (is.length == 2)
            actionMethod = is[1];
        else
            actionMethod = "execute";
        return actionName + "." + actionMethod;
    };
    /**解析调用字符串并且将解析之后的数据注入到ActionStack参数中，最后返回。actionName, actionMethod*/
    public static String[] splitInvokeString(String invokeString) {
        return formatInvokeString(invokeString).split("\\.");
    };
    /**解析调用字符串并且将解析之后的数据注入到ActionStack参数中，最后返回。actionName, actionMethod*/
    public static String getActionString(String invokeString) {
        return splitInvokeString(invokeString)[0];
    };
    /**解析调用字符串并且将解析之后的数据注入到ActionStack参数中，最后返回。actionName, actionMethod*/
    public static String getMethodString(String invokeString) {
        return splitInvokeString(invokeString)[1];
    };
};