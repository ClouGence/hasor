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
import java.util.Map;
/**
 * 该接口是一个可调用的action对象，通过接口方法可以对action进行调用。
 * @version : 2011-7-14
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionObject {
    /**获取action所处命名空间。*/
    public String getNameSpace();
    /**获取action字符串。*/
    public String getActionString();
    /**执行action，并且返回执行结果。*/
    public Object doAction(Object... objects) throws Throwable;
    /**执行action，并且返回执行结果。*/
    public Object doAction(Map<String, ?> params) throws Throwable;
    /**执行action，并且返回执行结果，新的{@link ActionStack}会基于在参数所表示的{@link ActionStack}之上。*/
    public Object doAction(ActionStack stack, Object... objects) throws Throwable;
    /**执行action，并且返回执行结果，新的{@link ActionStack}会基于在参数所表示的{@link ActionStack}之上。*/
    public Object doAction(ActionStack stack, Map<String, ?> params) throws Throwable;
};