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
/**
 * 该接口为一个命名空间提供对象索引服务。
 * @version : 2011-7-14
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionContext {
    /** 获取所有Action的对象名。*/
    public String[] getActionIDs();
    /**获取Bean对象，第二个参数是携带的用户查询信息。*/
    public ActionInvoke getAction(String name, String userInfo);
    /**定义一个action包，如果该包已经定义则返回定义的action包。*/
    public ActionPackage definePackage(String packageName);
}