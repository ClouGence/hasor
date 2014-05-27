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
package net.hasor.core.binder.register;
import net.hasor.core.AppContext;
import net.hasor.core.builder.BeanBuilder;
/**
 * 当开发者用过 {@link AppContext#getInstance(Class)}获取的Bean尚未在Hasor中注册的时候。
 * 该类型会被封装成{@link FreeTypeRegister}，然后在通过{@link BeanBuilder}接口创建。
 * <p>
 * {@link BeanBuilder}接口会根据不同的提供者来处理这种情形。如果提供者为Spring就会返回一个null。
 * 倘若是Guice就会得到你想要的Bean。
 * @version : 2014-3-20
 * @author 赵永春(zyc@hasor.net)
 */
public class FreeTypeRegister<T> extends AbstractTypeRegister<T> {
    public FreeTypeRegister(Class<T> type) {
        super(type);
        this.toImpl(type);
    }
}