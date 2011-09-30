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
 * Json互转时的检查接口，当检查通过就会使用与其注册的JsonParser进行解析。
 * @version : 2011-9-28
 * @author 赵永春 (zyc@byshell.org)
 */
public interface JsonCheck {
    /**检查目标对象是否可以转成所期望的字符串。*/
    public boolean checkToString(Object source);
    /**检查目标对象是否可以转成所期望的对象。*/
    public boolean checkToObject(String source);
}