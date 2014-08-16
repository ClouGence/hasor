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
package net.hasor.core.context.adapter;
import java.util.Iterator;
import net.hasor.core.BindInfo;
import net.hasor.core.BindInfoBuilder;
/**
 * 
 * @version : 2014-3-17
 * @author 赵永春(zyc@hasor.net)
 */
public interface RegisterFactory {
    /**创建一个绑定过类型*/
    public <T> T getInstance(BindInfo<T> oriType);
    /**创建一个未绑定过的类型*/
    public <T> T getDefaultInstance(Class<T> oriType);
    /**注册一个类型*/
    public <T> BindInfoBuilder<T> createTypeBuilder(Class<T> bindType);
    //
    /**根据Type查找RegisterInfo迭代器*/
    public <T> Iterator<? extends BindInfoBuilder<T>> getRegisterIterator(Class<T> bindType);
    /**查找所有RegisterInfo迭代器*/
    public Iterator<? extends BindInfoBuilder<?>> getRegisterIterator();
}