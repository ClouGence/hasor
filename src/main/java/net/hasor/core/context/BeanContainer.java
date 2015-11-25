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
package net.hasor.core.context;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.BindInfoBuilder;
import net.hasor.core.Provider;
import net.hasor.core.Scope;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.core.context.builder.TemplateBeanBuilder;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.scope.SingletonScope;
import org.more.RepeateException;
import org.more.classcode.MoreClassLoader;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 负责承载Hasor {@link AppContext}的状态数据。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public class BeanContainer extends TemplateBeanBuilder {
    protected Logger                                logger           = LoggerFactory.getLogger(getClass());
    private AtomicBoolean                           inited           = new AtomicBoolean(false);
    private ClassLoader                             rootLosder       = new MoreClassLoader();
    private Provider<Scope>                         singletonScope   = new InstanceProvider<Scope>(new SingletonScope());
    private List<BindInfo<?>>                       tempBindInfoList = new ArrayList<BindInfo<?>>();
    private ConcurrentHashMap<String, List<String>> indexTypeMapping = new ConcurrentHashMap<String, List<String>>();
    private ConcurrentHashMap<String, List<String>> indexNameMapping = new ConcurrentHashMap<String, List<String>>();
    private ConcurrentHashMap<String, BindInfo<?>>  idDataSource     = new ConcurrentHashMap<String, BindInfo<?>>();
    //
    //
    /*-----------------------------------------------------------------------------------BindInfo*/
    /**获取当创建Bean时使用的{@link ClassLoader}*/
    public ClassLoader getClassLoader() {
        return this.rootLosder;
    }
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
    /**获取类型下所有Name。*/
    public Collection<String> getBindInfoNamesByType(Class<?> targetClass) {
        return this.indexNameMapping.keySet();
    }
    /**
     * 创建一个{@link BindInfoBuilder}。
     * @param bindType 绑定类型
     * @return 返回 BindInfoBuilder。
     */
    public <T> AbstractBindInfoProviderAdapter<T> createBindInfoByType(Class<T> bindType) {
        AbstractBindInfoProviderAdapter<T> adapter = super.createBindInfoByType(bindType);
        this.tempBindInfoList.add(adapter);
        return adapter;
    }
    /*---------------------------------------------------------------------------------------Life*/
    public void doInitializeCompleted(AppContext context) {
        if (!this.inited.compareAndSet(false, true)) {
            return;/*避免被初始化多次*/
        }
        //
        for (BindInfo<?> info : this.tempBindInfoList) {
            String bindID = info.getBindID();
            //只有ID做重复检查
            if (idDataSource.containsKey(info.getBindID()) == true) {
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
                AbstractBindInfoProviderAdapter<?> infoAdapter = (AbstractBindInfoProviderAdapter<?>) info;
                if (infoAdapter.isSingleton() == true) {
                    if (infoAdapter.getScopeProvider() != null) {
                        throw new IllegalStateException("Single mode cannot be set scope.");
                    }
                    infoAdapter.setScopeProvider(this.singletonScope);
                }
            }
        }
        this.tempBindInfoList.clear();
    }
    public void doShutdownCompleted(AppContext appContext) {
        if (!this.inited.compareAndSet(true, false)) {
            return;/*避免被销毁多次*/
        }
        this.tempBindInfoList.clear();
        this.indexTypeMapping.clear();
        this.indexNameMapping.clear();
        this.idDataSource.clear();
        this.singletonScope = new InstanceProvider<Scope>(new SingletonScope());
    }
}