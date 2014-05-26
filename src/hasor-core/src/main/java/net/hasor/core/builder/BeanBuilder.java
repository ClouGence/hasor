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
package net.hasor.core.builder;
import java.util.Iterator;
import net.hasor.core.RegisterInfo;
/**
 * 
 * @version : 2014-3-17
 * @author 赵永春(zyc@hasor.net)
 */
public interface BeanBuilder {
    /**创建Bean*/
    public <T> T getInstance(RegisterInfo<T> oriType);
    /**获取所有注册Bean的迭代器*/
    public Iterator<RegisterInfo<?>> getRegisterIterator();
    /**获取制定类型注册Bean的迭代器*/
    public <T> Iterator<RegisterInfo<T>> getRegisterIterator(Class<T> type);
}