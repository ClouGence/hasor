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
package net.hasor.core.container;
import net.hasor.core.*;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.scope.SingletonScope;
import org.more.RepeateException;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 整个Hasor将围绕这个类构建！！
 * <br/>它，完成了Bean容器的功能。
 * <br/>它，完成了依赖注入的功能。
 * <br/>它，完成了Aop的功能。
 * <br/>它，支持了{@link Scope}作用域功能。
 * <br/>它，支持了{@link AppContext}接口功能。
 * <br/>它，是万物之母，一切生命的源泉。
 * @version : 2015年11月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class BeanContainer extends TemplateBeanBuilder implements ScopManager {
    protected Logger                                     logger           = LoggerFactory.getLogger(getClass());
    private   AtomicBoolean                              inited           = new AtomicBoolean(false);
    //    private   Scope                                      singletonScope   = new SingletonScope();
    private   List<BindInfo<?>>                          tempBindInfoList = new ArrayList<BindInfo<?>>();
    private   ConcurrentHashMap<String, List<String>>    indexTypeMapping = new ConcurrentHashMap<String, List<String>>();
    private   ConcurrentHashMap<String, List<String>>    indexNameMapping = new ConcurrentHashMap<String, List<String>>();
    private   ConcurrentHashMap<String, BindInfo<?>>     idDataSource     = new ConcurrentHashMap<String, BindInfo<?>>();
    private   ConcurrentHashMap<String, Provider<Scope>> scopeMapping     = new ConcurrentHashMap<String, Provider<Scope>>();
    //
    //
    /*-----------------------------------------------------------------------------------BindInfo*/
    public boolean isInit() {
        return this.inited.get();
    }
    /**根据ID查找{@link BindInfo}*/
    public <T> BindInfo<T> getBindInfoByID(String infoID) {
        return (BindInfo<T>) this.idDataSource.get(infoID);
    }
    /**根据绑定的类型找到所有类型相同的{@link BindInfo}*/
    public <T> List<BindInfo<T>> getBindInfoByType(Class<T> targetType) {
        List<String> idList = this.indexTypeMapping.get(targetType.getName());
        if (idList == null || idList.isEmpty()) {
            logger.debug("getBindInfoByType , never define this type = {}", targetType);
            return Collections.EMPTY_LIST;
        }
        List<BindInfo<T>> resultList = new ArrayList<BindInfo<T>>();
        for (String infoID : idList) {
            BindInfo<?> adapter = this.idDataSource.get(infoID);
            if (adapter != null) {
                resultList.add((BindInfo<T>) adapter);
            } else {
                logger.debug("getBindInfoByType , cannot find {} BindInfo.", infoID);
            }
        }
        return resultList;
    }
    /**根据名称找到同名的所有{@link BindInfo}*/
    public List<BindInfo<?>> getBindInfoByName(String bindName) {
        List<String> nameList = this.indexNameMapping.get(bindName);
        if (nameList == null || nameList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<BindInfo<?>> resultList = new ArrayList<BindInfo<?>>();
        for (String infoName : nameList) {
            BindInfo<?> adapter = this.idDataSource.get(infoName);
            if (adapter != null) {
                resultList.add(adapter);
            }
        }
        return resultList;
    }
    /**获取所有ID。*/
    public Collection<String> getBindInfoIDs() {
        return this.idDataSource.keySet();
    }
    /**
     * 获取类型下所有Name
     * @param targetClass 类型
     * @return 返回声明类型下有效的名称。
     */
    public Collection<String> getBindInfoNamesByType(Class<?> targetClass) {
        return this.indexNameMapping.keySet();
    }
    /**
     * 创建{@link AbstractBindInfoProviderAdapter}，交给外层用于Bean定义。
     * @param bindType 声明的类型。
     */
    public <T> AbstractBindInfoProviderAdapter<T> createInfoAdapter(Class<T> bindType) {
        AbstractBindInfoProviderAdapter<T> adapter = super.createInfoAdapter(bindType);
        this.tempBindInfoList.add(adapter);
        return adapter;
    }
    @Override
    protected <T> T createObject(final Class<T> targetType, final BindInfo<T> bindInfo, final AppContext appContext) {
        boolean isSingleton = testSingleton(targetType, bindInfo, appContext.getEnvironment().getSettings());
        //
        if (isSingleton) {
            Object key = (bindInfo != null) ? bindInfo : targetType;
            Provider<Scope> singleton = Hasor.assertIsNotNull(this.scopeMapping.get(ScopManager.SINGLETON_SCOPE));
            return singleton.get().scope(key, new Provider<T>() {
                public T get() {
                    return callSuperCreateObject(targetType, bindInfo, appContext);
                }
            }).get();
            //   
        } else {
            return callSuperCreateObject(targetType, bindInfo, appContext);
        }
    }
    private <T> T callSuperCreateObject(Class<T> targetType, BindInfo<T> bindInfo, AppContext appContext) {
        return super.createObject(targetType, bindInfo, appContext);
    }
    /*---------------------------------------------------------------------------------------Life*/
    /**
     * 当容器启动时，需要做Bean注册的重复性检查。
     */
    public void doInitializeCompleted(Environment env) {
        if (!this.inited.compareAndSet(false, true)) {
            return;/*避免被初始化多次*/
        }
        //
        for (BindInfo<?> info : this.tempBindInfoList) {
            String bindID = info.getBindID();
            //只有ID做重复检查
            if (idDataSource.containsKey(info.getBindID())) {
                throw new RepeateException("duplicate bind id value is " + info.getBindID());
            }
            idDataSource.put(bindID, info);
            //
            String bindTypeStr = info.getBindType().getName();
            List<String> newTypeList = new ArrayList<String>();
            List<String> typeList = indexTypeMapping.putIfAbsent(bindTypeStr, newTypeList);
            if (typeList == null) {
                typeList = newTypeList;
            }
            typeList.add(bindID);
            //
            String bindName = info.getBindName();
            bindName = StringUtils.isBlank(bindName) ? "" : bindName;
            List<String> newNameList = new ArrayList<String>();
            List<String> nameList = indexNameMapping.putIfAbsent(bindName, newNameList);
            if (nameList == null) {
                nameList = newNameList;
            }
            nameList.add(bindName);
            //
            if (info instanceof AbstractBindInfoProviderAdapter) {
                final AbstractBindInfoProviderAdapter<?> infoAdapter = (AbstractBindInfoProviderAdapter<?>) info;
                Method initMethod = TemplateBeanBuilder.findInitMethod(info.getBindType(), infoAdapter);
                boolean singleton = testSingleton(infoAdapter.getBindType(), info, env.getSettings());
                if (initMethod != null && singleton) {
                    //
                    Hasor.pushStartListener(env, new EventListener<AppContext>() {
                        public void onEvent(String event, AppContext eventData) throws Throwable {
                            eventData.getInstance(infoAdapter);//执行init
                        }
                    });
                }
            }
        }
        this.tempBindInfoList.clear();
        this.scopeMapping.put(ScopManager.SINGLETON_SCOPE, new InstanceProvider<Scope>(new SingletonScope()));
    }
    /**
     * 当容器停止运行时，需要做Bean清理工作。
     */
    public void doShutdownCompleted() {
        if (!this.inited.compareAndSet(true, false)) {
            return;/*避免被销毁多次*/
        }
        this.tempBindInfoList.clear();
        this.indexTypeMapping.clear();
        this.indexNameMapping.clear();
        this.idDataSource.clear();
        this.scopeMapping.clear();
    }
    //
    @Override
    public Provider<Scope> registerScope(String scopeName, Provider<Scope> scope) {
        Provider<Scope> oldScope = this.scopeMapping.putIfAbsent(scopeName, scope);
        if (oldScope == null) {
            oldScope = scope;
        }
        return oldScope;
    }
    @Override
    public Provider<Scope> findScope(String scopeName) {
        return this.scopeMapping.get(scopeName);
    }
}