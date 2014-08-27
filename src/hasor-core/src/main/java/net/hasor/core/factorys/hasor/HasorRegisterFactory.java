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
package net.hasor.core.factorys.hasor;
import net.hasor.core.BindInfo;
import net.hasor.core.BindInfoDefineManager;
import net.hasor.core.factorys.AbstractBindInfoFactory;
import net.hasor.core.factorys.BaseBindInfoDefineManager;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import org.more.classcode.ClassEngine;
/**
 * 
 * @version : 2014年7月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class HasorRegisterFactory extends AbstractBindInfoFactory {
    protected BindInfoDefineManager createDefineManager() {
        return new BaseBindInfoDefineManager() {
            protected <T> AbstractBindInfoProviderAdapter<T> createRegisterInfoAdapter(Class<T> bindingType) {
                return new HasorBindInfoProviderAdapter<T>(bindingType);
            }
        };
    }
    public <T> T getInstance(BindInfo<T> bindInfo) {
        if (bindInfo instanceof HasorBindInfoProviderAdapter == false) {
            return this.getDefaultInstance(bindInfo.getBindType());
        }
        //
        HasorBindInfoProviderAdapter<T> infoAdapter = (HasorBindInfoProviderAdapter<T>) bindInfo;
        ClassEngine engine = infoAdapter.buildEngine();
        try {
            Class<?> newType = engine.builderClass().toClass();
            Object targetBean = newType.newInstance();
            return (T) engine.configBean(targetBean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    protected void configBindInfo(AbstractBindInfoProviderAdapter<Object> bindInfo, Object context) {
        // TODO Auto-generated method stub
    }
}