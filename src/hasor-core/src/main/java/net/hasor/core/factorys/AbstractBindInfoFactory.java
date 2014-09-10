/*
 * Copyright 2008-2009 the original author or authors.
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.BindInfo;
import net.hasor.core.BindInfoDefineManager;
import net.hasor.core.context.AbstractAppContext;
import net.hasor.core.context.listener.ContextInitializeListener;
import net.hasor.core.context.listener.ContextStartListener;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import org.more.util.ArrayUtils;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
/**
 * RegisterFactory接口的默认实现，包含了一些检查过程。
 * @version : 2014-5-10
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractBindInfoFactory implements BindInfoFactory, AppContextAware, ContextInitializeListener, ContextStartListener {
    private BindInfoDefineManager defineManager = null;
    private AppContext            appContext    = null;
    //
    public BindInfoDefineManager getManager() {
        if (this.defineManager == null) {
            this.defineManager = this.createDefineManager();
        }
        if (this.defineManager == null) {
            throw new NullPointerException("BuilderRegister is null.");
        }
        return this.defineManager;
    }
    /**获得所处的{@link AppContext}对象。*/
    public AppContext getAppContext() {
        return this.appContext;
    }
    /**注入AppContext*/
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    /**创建{@link BindInfoDefineManager}对象。*/
    protected BindInfoDefineManager createDefineManager() {
        return new AbstractBindInfoDefineManager() {};
    }
    //
    /**创建一个未绑定过的类型*/
    public <T> T getDefaultInstance(final Class<T> oriType) {
        if (oriType == null) {
            return null;
        }
        try {
            if (oriType.isInterface() || oriType.isEnum()) {
                return null;
            }
            if (oriType.isPrimitive()) {
                return (T) BeanUtils.getDefaultValue(oriType);
            }
            if (oriType.isArray()) {
                Class<?> comType = oriType.getComponentType();
                return (T) Array.newInstance(comType, 0);
            }
            return oriType.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**创建 {@link BindInfo}所表示的那个类型。
     * @see #getInstance(BindInfo)*/
    public abstract <T> T getInstance(BindInfo<T> bindInfo);
    //
    public String[] getNamesOfType(Class<?> bindType) {
        Iterator<? extends AbstractBindInfoProviderAdapter<?>> adapterList = this.defineManager.getBindInfoIterator(bindType);
        if (adapterList == null || adapterList.hasNext() == false) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List<String> names = new ArrayList<String>();
        while (adapterList.hasNext()) {
            AbstractBindInfoProviderAdapter<?> adapter = adapterList.next();
            String name = adapter.getBindName();
            if (StringUtils.isBlank(name) == false) {
                names.add(name);
            }
        }
        return names.toArray(new String[names.size()]);
    }
    public <T> BindInfo<T> getBindInfo(String withName, Class<T> bindType) {
        Iterator<? extends AbstractBindInfoProviderAdapter<?>> adapterList = this.defineManager.getBindInfoIterator(bindType);
        if (adapterList == null || adapterList.hasNext() == false) {
            return null;
        }
        while (adapterList.hasNext()) {
            AbstractBindInfoProviderAdapter<?> adapter = adapterList.next();
            boolean eq = StringUtils.equals(adapter.getBindName(), withName);
            if (eq == true) {
                return (BindInfo<T>) adapter;
            }
        }
        return null;
    }
    //
    public void doInitialize(ApiBinder apiBinder) {
        // TODO Auto-generated method stub
    }
    public void doInitializeCompleted(Object context) {
        this.defineManager.doFinish();/*数据检测*/
        Iterator<AbstractBindInfoProviderAdapter<?>> registerIterator = getManager().getBindInfoIterator();
        while (registerIterator.hasNext()) {
            AbstractBindInfoProviderAdapter<Object> register = (AbstractBindInfoProviderAdapter<Object>) registerIterator.next();
            configBindInfo(register, context);
        }
    }
    public void doStart(final AbstractAppContext appContext) {
        // TODO Auto-generated method stub
    }
    public void doStartCompleted(final AbstractAppContext appContext) {
        // TODO Auto-generated method stub
    }
    //
    protected abstract void configBindInfo(AbstractBindInfoProviderAdapter<Object> bindInfo, Object context);
}