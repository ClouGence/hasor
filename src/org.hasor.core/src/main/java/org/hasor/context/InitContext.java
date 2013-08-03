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
package org.hasor.context;
import java.util.Set;
/**
 * 初始化使其的应用程序上下文
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public interface InitContext {
    /**获取上下文*/
    public Object getContext();
    /**获取系统启动时间*/
    public long getAppStartTime();
    /**获取应用程序配置。*/
    public Settings getSettings();
    /**获取环境变量操作接口。*/
    public Environment getEnvironment();
    /**同步方式抛出事件。当方法返回时已经全部处理完成事件分发。*/
    public EventManager getEventManager();
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> getClassSet(Class<?> featureType);
}