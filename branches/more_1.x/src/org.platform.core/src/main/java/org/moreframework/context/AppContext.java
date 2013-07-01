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
package org.moreframework.context;
import java.util.Set;
import com.google.inject.Injector;
/**
 * 
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public interface AppContext extends BeanContext {
    /**获取上下文*/
    public Object getContext();
    /**获取系统启动时间*/
    public long getAppStartTime();
    /**获取应用程序配置。*/
    public Settings getSettings();
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> getClassSet(Class<?> featureType);
    /**通过类型创建该类实例，使用guice*/
    public <T> T getInstance(Class<T> beanType);
    //    /**通过名称创建bean实例，使用guice。*/
    //    public  <T extends IService> T getService(String servicesName);
    //    /**通过类型创建该类实例，使用guice*/
    //    public  <T extends IService> T getService(Class<T> servicesType);
    /**获得Guice环境。*/
    public Injector getGuice();
    /**获得工作空间设置*/
    public WorkSpace getWorkSpace();
    /**获取环境变量操作接口。*/
    public Environment getEnvironment();
}