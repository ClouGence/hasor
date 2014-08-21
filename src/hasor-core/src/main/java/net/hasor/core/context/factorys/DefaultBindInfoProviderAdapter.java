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
package net.hasor.core.context.factorys;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
/**
 * 
 * @version : 2014年7月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultBindInfoProviderAdapter<T> extends AbstractBindInfoProviderAdapter<T> {
    public DefaultBindInfoProviderAdapter() {}
    public DefaultBindInfoProviderAdapter(Class<T> bindingType) {
        this.setBindType(bindingType);
    }
    @Override
    public void setInitParam(final int index, final Class<?> paramType, final Provider<?> valueProvider) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void setInitParam(final int index, final Class<?> paramType, final BindInfo<?> valueInfo) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void addInject(final String property, final Provider<?> valueProvider) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void addInject(final String property, final BindInfo<?> valueInfo) {
        throw new UnsupportedOperationException();
    }
}