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
/**
 * 
 * @version : 2014-3-17
 * @author 赵永春(zyc@hasor.net)
 */
public interface BindInfoFactory {
    /**
     * 获取注册器
     * @return 返回BindInfoDefineManager
     */
    public BindInfoDefineManager getManager();
    //
    /**
     * 通过{@link BindInfo}创建Bean。
     * @param bindInfo 绑定信息。
     * @return 创建并返回实例
     */
    public <T> T getInstance(BindInfo<T> bindInfo);
    /**
     * 通过绑定类型创建Bean。
     * @param bindType 绑定类型。
     * @return 创建并返回实例
     */
    public <T> T getDefaultInstance(Class<T> bindType);
    /**
     * 获取绑定类型下所有name。
     * @param bindType 绑定类型。
     * @return 返回所有name。
     */
    public String[] getNamesOfType(Class<?> bindType);
    /**
     * 根据名称和类型获取获取{@link BindInfo}。
     * @param withName 绑定名称
     * @param bindType 绑定类型
     * @return 返回{@link BindInfo}。
     */
    public <T> BindInfo<T> getBindInfo(String withName, Class<T> bindType);
}