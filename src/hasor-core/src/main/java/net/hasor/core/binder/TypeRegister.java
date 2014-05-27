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
package net.hasor.core.binder;
import java.lang.reflect.Constructor;
import net.hasor.core.Provider;
import net.hasor.core.RegisterInfo;
import net.hasor.core.Scope;
/**
 * 
 * @version : 2014-3-13
 * @author 赵永春(zyc@hasor.net)
 */
public interface TypeRegister<T> extends RegisterInfo<T> {
    /**获取注册的类型*/
    public Class<T> getType();
    /**为类型绑定一个实现，当获取类型实例时其实获取的是实现对象。*/
    public void toImpl(Class<? extends T> implementation);
    /**为类型绑定一个实现对象。*/
    public void toInstance(T instance);
    /**为类型绑定一个Provider。*/
    public void toProvider(Provider<T> provider);
    /**为类型绑定一个初始构造方法。*/
    public void toConstructor(Constructor<? extends T> constructor);
    /**为类型绑定一个名称。*/
    public void setName(String name);
    /**将类型发布为单例模式。*/
    public void setSingleton();
    /**将类型发布到一个固定的命名空间内。*/
    public void setScope(Scope scope);
}