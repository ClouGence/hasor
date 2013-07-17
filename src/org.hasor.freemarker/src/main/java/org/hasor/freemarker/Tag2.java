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
package org.hasor.freemarker;
import java.util.Map;
/***
 * 标签接口{@link Tag}的增强接口，使用该接口将不会在调用标签的get/set方法设置属性。
 * @version : 2013-5-17
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Tag2 extends Tag {
    /***/
    public void setup(Map<String, Object> objMap);
}