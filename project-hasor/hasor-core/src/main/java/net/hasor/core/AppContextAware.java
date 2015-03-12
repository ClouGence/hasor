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
package net.hasor.core;
/**
 * 当 AppContext 启动的第一时间。容器会通知这个接口的实现类，将AppContext注入进来。<p>
 * 使用它，需要实现这个接口并通过{@link ApiBinder#autoAware(AppContextAware)}方法注册。
 * @see ApiBinder#autoAware(AppContextAware)
 * @version : 2013-11-8
 * @author 赵永春(zyc@hasor.net)
 */
public interface AppContextAware {
    /**
     * 注入AppContext。
     * @param appContext 注入的AppContext。
     */
    public void setAppContext(AppContext appContext);
}