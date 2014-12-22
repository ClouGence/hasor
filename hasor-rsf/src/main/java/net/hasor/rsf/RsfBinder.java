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
package net.hasor.rsf;
import net.hasor.core.Provider;
/**
 * 服务注册器
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfBinder {
    /**添加全局的RsfFilter。*/
    public void bindFilter(RsfFilter instance);
    /**添加全局的RsfFilter。*/
    public void bindFilter(Provider<RsfFilter> provider);
    /** */
    public <T> LinkedBuilder<T> rsfService(Class<T> type);
    /**将后面的对象绑定前一个类型上，可以通过AppContext获取该绑定对象。
     * @see #rsfService(Class) */
    public <T> NamedBuilder<T> rsfService(Class<T> type, T instance);
    /**将后面的对象绑定前一个类型上，可以通过AppContext获取该绑定对象。
     * @see #rsfService(Class) */
    public <T> NamedBuilder<T> rsfService(Class<T> type, Class<? extends T> implementation);
    /**将后面的对象绑定前一个类型上，可以通过AppContext获取该绑定对象。
     * @see #rsfService(Class) */
    public <T> NamedBuilder<T> rsfService(Class<T> type, Provider<T> provider);
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
    /**设置服务名。*/
    public interface NamedBuilder<T> extends ConfigurationBuilder<T> {
        /**设置分组。*/
        public ConfigurationBuilder<T> ngv(String name, String group, String version);
    }
    /**设置参数。*/
    public interface ConfigurationBuilder<T> extends RegisterBuilder<T> {
        /**设置超时时间*/
        public ConfigurationBuilder<T> timeout(int clientTimeout);
        /**设置序列化方式*/
        public ConfigurationBuilder<T> serialize(String serializeType);
        /**添加RsfFilter。*/
        public ConfigurationBuilder<T> bindFilter(RsfFilter instance);
        /**添加RsfFilter。*/
        public ConfigurationBuilder<T> bindFilter(Provider<RsfFilter> provider);
    }
    /**绑定元信息*/
    public interface RegisterBuilder<T> {
        /**绑定远程服务地址和端口。*/
        public RegisterBuilder<T> addBindAddress(String remoteHost, int remotePort);
        /**将服务注册到{@link RsfContext}上。*/
        public RegisterReference<T> register();
    }
    /**可以用于解除注册的接口。*/
    public interface RegisterReference<T> extends RsfBindInfo<T> {
        /**解除注册。*/
        public void unRegister();
    }
}