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
package net.hasor.core.context.factorys.hasor;
import net.hasor.core.RegisterInfo;
import net.hasor.core.context.factorys.AbstractRegisterFactory;
import net.hasor.core.context.factorys.AbstractRegisterInfoAdapter;
import org.more.classcode.ClassEngine;
/**
 * 
 * @version : 2014年7月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class HasorRegisterFactory extends AbstractRegisterFactory {
    @Override
    public <T> T getDefaultInstance(Class<T> oriType) {
        return super.getDefaultInstance(oriType);
    }
    protected <T> AbstractRegisterInfoAdapter<T> createRegisterInfoAdapter(Class<T> bindingType) {
        return new HasorRegisterInfoAdapter<T>(bindingType);
    }
    @Override
    protected <T> T newInstance(final RegisterInfo<T> oriType) {
        if (oriType instanceof HasorRegisterInfoAdapter == false) {
            return this.getDefaultInstance(oriType.getBindType());
        }
        //
        HasorRegisterInfoAdapter<T> infoAdapter = (HasorRegisterInfoAdapter<T>) oriType;
        ClassEngine engine = infoAdapter.buildEngine();
        try {
            Class<?> newType = engine.builderClass().toClass();
            Object targetBean = newType.newInstance();
            return (T) engine.configBean(targetBean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}