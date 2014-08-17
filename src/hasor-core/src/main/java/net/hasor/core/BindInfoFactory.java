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
import java.lang.reflect.Method;
import net.hasor.core.ApiBinder.Matcher;
/**
 * 
 * @version : 2014-3-17
 * @author 赵永春(zyc@hasor.net)
 */
public interface BindInfoFactory {
    /**注册一个类型*/
    public <T> BindInfoBuilder<T> createTypeBuilder(Class<T> bindType);
    /**注册一个Aop*/
    public void registerAop(Matcher<Class<?>> matcherClass, Matcher<Method> matcherMethod, MethodInterceptor interceptor);
    //
    /**创建一个绑定过类型*/
    public <T> T getInstance(BindInfo<T> oriType);
    /**创建一个未绑定过的类型*/
    public <T> T getDefaultInstance(Class<T> oriType);
    /**获取类型绑定的所有名字。*/
    public String[] getNamesOfType(Class<?> bindType);
    /**根据名称和类型获取获取{@link BindInfo}。*/
    public <T> BindInfo<T> getRegister(String withName, Class<T> bindType);
}