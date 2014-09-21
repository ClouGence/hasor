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
package net.hasor.core.factorys;
import net.hasor.core.BindInfo;
import net.hasor.core.BindInfoDefineManager;
/**
 * 
 * @version : 2014-3-17
 * @author 赵永春(zyc@hasor.net)
 */
public interface BindInfoFactory {
    /**获取注册器*/
    public BindInfoDefineManager getManager();
    //
    /**创建一个绑定过类型*/
    public <T> T getInstance(BindInfo<T> oriType);
    /**创建一个未绑定过的类型*/
    public <T> T getDefaultInstance(Class<T> oriType);
    /**获取类型绑定的所有名字。*/
    public String[] getNamesOfType(Class<?> bindType);
    /**根据名称和类型获取获取{@link BindInfo}。*/
    public <T> BindInfo<T> getBindInfo(String withName, Class<T> bindType);
}