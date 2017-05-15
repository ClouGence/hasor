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
package net.hasor.core;
/**
 * 初始化注入接口。Hasor 的 Ioc 是通过递归的方式实现，版本中要想实依赖注入必须要实现 InjectMembers接口。
 * 请注意：{@link Inject}注解方式和接口方式互斥，且接口方式优先于注解方式。
 * @version : 2014-5-10
 * @author 赵永春 (zyc@byshell.org)
 */
public interface InjectMembers {
    /**
     * 执行注入
     * @param appContext appContext对象
     */
    public void doInject(AppContext appContext) throws Throwable;
}