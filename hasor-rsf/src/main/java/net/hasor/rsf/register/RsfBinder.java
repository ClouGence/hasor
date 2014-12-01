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
package net.hasor.rsf.register;
import net.hasor.core.Provider;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.RsfFilter;
/**
 * 服务注册器
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfBinder {
    /**添加全局的RsfFilter。*/
    public void addRsfFilter(RsfFilter instance);
    /**添加全局的RsfFilter。*/
    public void addRsfFilter(Provider<RsfFilter> provider);
    /** */
    public <T> LinkedBuilder<T> bindService(Class<T> type);
    /**将后面的对象绑定前一个类型上，可以通过AppContext获取该绑定对象。
     * @see #bindService(Class) */
    public <T> NamedBuilder<T> bindService(Class<T> type, T instance);
    /**将后面的对象绑定前一个类型上，可以通过AppContext获取该绑定对象。
     * @see #bindService(Class) */
    public <T> NamedBuilder<T> bindService(Class<T> type, Class<? extends T> implementation);
    /**将后面的对象绑定前一个类型上，可以通过AppContext获取该绑定对象。
     * @see #bindService(Class) */
    public <T> NamedBuilder<T> bindService(Class<T> type, Provider<T> provider);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see #rsfService(Class) */
    public <T> ConfigurationBuilder<T> bindService(String withName, Class<T> type);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see #bindService(String, Class) */
    public <T> ConfigurationBuilder<T> bindService(String withName, Class<T> type, T instance);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see #bindService(String, Class) */
    public <T> ConfigurationBuilder<T> bindService(String withName, Class<T> type, Class<? extends T> implementation);
    /**为绑定的对象指定一个名称进行绑定，相同名称的类型绑定只能绑定一次。
     * @see #bindService(String, Class) */
    public <T> ConfigurationBuilder<T> bindService(String withName, Class<T> type, Provider<T> provider);
    //
    /**处理类型和实现的绑定。*/
    public interface LinkedBuilder<T> extends NamedBuilder<T> {
        /**为绑定设置一个实现类。*/
        public NamedBuilder<T> to(Class<? extends T> implementation);
        /**为绑定设置一个实例。*/
        public NamedBuilder<T> toInstance(T instance);
        /**为绑定设置一个 {@link Provider}。*/
        public NamedBuilder<T> toProvider(Provider<T> provider);
    }
    /**给绑定起个名字。*/
    public interface NamedBuilder<T> extends ConfigurationBuilder<T> {
        /**设置服务名称。*/
        public ConfigurationBuilder<T> nameWith(String name);
    }
    /**设置参数。*/
    public interface ConfigurationBuilder<T> extends MetaDataBuilder<T> {
        /**设置分组。*/
        public ConfigurationBuilder<T> groupWith(String group);
        /**设置版本。*/
        public ConfigurationBuilder<T> versionWith(String version);
        /**设置超时时间*/
        public ConfigurationBuilder<T> timeout(int clientTimeout);
        /**设置序列化方式*/
        public ConfigurationBuilder<T> serialize(String serializeType);
        /**添加RsfFilter。*/
        public ConfigurationBuilder<T> addRsfFilter(RsfFilter instance);
        /**添加RsfFilter。*/
        public ConfigurationBuilder<T> addRsfFilter(Provider<RsfFilter> provider);
    }
    /**绑定元信息*/
    public interface MetaDataBuilder<T> {
        /**转换为 {@link ServiceMetaData} 对象。*/
        public ServiceMetaData getMetaData();
        /**将服务注册到{@link RsfContext}上。*/
        public void register();
        /**解除注册。*/
        public void unRegister();
    }
}