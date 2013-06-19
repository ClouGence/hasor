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
/**
 * 为提供Json格式数据互转的基类。
 * @version 2010-1-7
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class JsonParser {
    private JsonUtil currentContext;
    protected JsonParser(JsonUtil currentContext) {
        this.currentContext = currentContext;
    };
    protected JsonUtil getCurrentContext() {
        return currentContext;
    };
    /**转换对象为json字符串*/
    public abstract String toString(Object bean);
    /**将字符串转换为json对象*/
    public abstract Object toObject(String str);
};