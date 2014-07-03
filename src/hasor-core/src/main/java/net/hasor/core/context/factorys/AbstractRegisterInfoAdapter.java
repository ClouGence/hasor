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
import java.util.HashMap;
import java.util.Map;
import net.hasor.core.binder.TypeBuilder;
import net.hasor.core.context.adapter.RegisterInfoAdapter;
/**
 * 
 * @version : 2014年7月3日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRegisterInfoAdapter<T> implements RegisterInfoAdapter<T>, TypeBuilder<T> {
    private String              bindName = null;
    private Class<T>            bindType = null;
    private Map<String, Object> metaData = new HashMap<String, Object>();
    //
    public AbstractRegisterInfoAdapter(Class<T> bindType) {
        this.bindType = bindType;
    }
    public void setName(String bindName) {
        this.bindName = bindName;
    }
    public String getName() {
        return this.bindName;
    }
    public Class<T> getType() {
        return this.bindType;
    }
    public void setMetaData(String key, Object value) {
        this.metaData.put(key, value);
    }
    public Object getMetaData(String key) {
        return this.metaData.get(key);
    }
    //
    //
    //    public void setSourceType(Class<? extends T> implementation) {
    //        // TODO Auto-generated method stub
    //    }
    //    public void setSingleton(boolean singleton) {
    //        // TODO Auto-generated method stub
    //    }
    //    public void setProvider(Provider<T> provider) {
    //        // TODO Auto-generated method stub
    //    }
    //    public void setScope(Provider<Scope> scope) {
    //        // TODO Auto-generated method stub
    //    }
    //    public void setInitParam(int index, Class<?> paramType, Provider<?> valueProvider) {
    //        // TODO Auto-generated method stub
    //    }
    //    public void setInitParam(int index, Class<?> paramType, RegisterInfo<?> valueInfo) {
    //        // TODO Auto-generated method stub
    //    }
    //    public void addInject(String property, Provider<?> valueProvider) {
    //        // TODO Auto-generated method stub
    //    }
    //    public void addInject(String property, RegisterInfo<?> valueInfo) {
    //        // TODO Auto-generated method stub
    //    }
}