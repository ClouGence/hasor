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
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.info.DefaultBindInfoProviderAdapter;
import net.hasor.core.info.NotifyData;
import net.hasor.core.spi.BindInfoProvisionListener;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 负责管理 Bean 的元信息
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019年06月20日
 */
public class BindInfoContainer extends AbstractContainer implements Observer {
    protected static Logger                                  logger             = LoggerFactory.getLogger(BindInfoContainer.class);
    private          List<BindInfo<?>>                       allBindInfoList    = new ArrayList<>();
    private          ConcurrentHashMap<String, List<String>> indexTypeMapping   = new ConcurrentHashMap<>();
    private          ConcurrentHashMap<String, BindInfo<?>>  idDataSource       = new ConcurrentHashMap<>();
    private          SpiCallerContainer                      spiCallerContainer = null;

    public BindInfoContainer(SpiCallerContainer spiCallerContainer) {
        this.spiCallerContainer = spiCallerContainer;
    }

    /*-----------------------------------------------------------------------------------BindInfo*/

    /**
     * 根据ID获取{@link BindInfo}。
     */
    public <T> BindInfo<T> findBindInfo(String infoID) {
        return (BindInfo<T>) this.idDataSource.get(infoID);
    }

    /**
     * 通过一个类型获取所有绑定该类型下的绑定信息。
     *
     * @param bindType bean type
     * @return 返回所有符合条件的绑定信息。
     */
    public <T> List<BindInfo<T>> findBindInfoList(final Class<T> bindType) {
        List<String> idList = this.indexTypeMapping.get(bindType.getName());
        if (idList == null || idList.isEmpty()) {
            logger.debug("getBindInfoByType , never define this type = {}", bindType);
            return Collections.emptyList();
        }
        List<BindInfo<T>> resultList = new ArrayList<>();
        for (String infoID : idList) {
            BindInfo<?> adapter = this.idDataSource.get(infoID);
            if (adapter != null) {
                resultList.add((BindInfo<T>) adapter);
            }
        }
        return resultList;
    }

    /**
     * 通过一个类型获取所有绑定该类型下的绑定信息。
     *
     * @param withName 绑定名
     * @param bindType bean type
     * @return 返回所有符合条件的绑定信息。
     */
    public <T> BindInfo<T> findBindInfo(final String withName, final Class<T> bindType) {
        Objects.requireNonNull(bindType, "bindType is null.");
        //
        List<BindInfo<T>> typeRegisterList = findBindInfoList(bindType);
        if (typeRegisterList != null && !typeRegisterList.isEmpty()) {
            for (int i = typeRegisterList.size() - 1; i >= 0; i--) {
                BindInfo<T> adapter = typeRegisterList.get(i);
                String bindName = adapter.getBindName();
                if (StringUtils.isBlank(bindName) && StringUtils.isBlank(withName)) {
                    return adapter;
                }
                if (bindName != null && bindName.equals(withName)) {
                    return adapter;
                }
            }
        }
        return null;
    }

    /** 获取所有ID。 */
    public Collection<String> getBindInfoIDs() {
        return this.idDataSource.keySet();
    }

    /*-------------------------------------------------------------------------------------------*/

    /**
     * 创建{@link DefaultBindInfoProviderAdapter}，交给外层用于Bean定义。
     * @param bindType 声明的类型。
     */
    public <T> DefaultBindInfoProviderAdapter<T> createInfoAdapter(Class<T> bindType, ApiBinder apiBinder) {
        if (this.isInit()) {
            throw new IllegalStateException("container has been started.");
        }
        // .构造 BindInfo
        DefaultBindInfoProviderAdapter<T> adapter = new DefaultBindInfoProviderAdapter<>(bindType);
        adapter.addObserver(this);
        adapter.setBindID(adapter.getBindID());
        // .触发 SPI
        this.spiCallerContainer.callSpi(BindInfoProvisionListener.class, listener -> {
            listener.newBindInfo(adapter, apiBinder);
        });
        return adapter;
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        // - 处理当异常发生时，新的 Bean 定义回滚逻辑
        try {
            this.doUpdate(o, arg);
        } catch (RuntimeException e) {
            BindInfo<?> bindInfo = (BindInfo<?>) o;
            this.idDataSource.remove(bindInfo.getBindID());
            this.allBindInfoList.remove(o);
            List<String> stringList = this.indexTypeMapping.get(bindInfo.getBindType().getName());
            if (stringList != null) {
                stringList.remove(bindInfo.getBindID());
            }
            throw e;
        }
    }

    private void doUpdate(Observable o, Object arg) {
        if (!(arg instanceof NotifyData)) {
            return;
        }
        if (!(o instanceof AbstractBindInfoProviderAdapter)) {
            return;
        }
        //
        AbstractBindInfoProviderAdapter target = (AbstractBindInfoProviderAdapter) o;
        String bindTypeStr = target.getBindType().getName();
        String bindID = target.getBindID();
        //
        // .新数据初始化
        boolean hasOld = this.idDataSource.containsKey(bindID);
        if (!hasOld) {
            this.allBindInfoList.add(target);
            this.idDataSource.put(bindID, target);
            List<String> newTypeList = new ArrayList<>();
            List<String> typeList = indexTypeMapping.putIfAbsent(bindTypeStr, newTypeList);
            if (typeList == null) {
                typeList = newTypeList;
            }
            typeList.add(bindID);
        }
        // .
        NotifyData notifyData = (NotifyData) arg;
        Object oldValue = notifyData.getOldValue();
        Object newValue = notifyData.getNewValue();
        if ((newValue == null && oldValue == null) || (newValue != null && newValue.equals(oldValue))) {
            return;/*没有变化*/
        }
        // .
        if ("bindID".equalsIgnoreCase(notifyData.getKey())) {
            newValue = Objects.requireNonNull(newValue);
            if (this.idDataSource.containsKey(newValue)) {
                throw new IllegalStateException("duplicate bind -> id value is " + newValue);
            }
            this.idDataSource.put((String) newValue, target);
            this.idDataSource.remove(oldValue);
            List<String> idList = this.indexTypeMapping.get(target.getBindType().getName());
            if (idList == null) {
                throw new IllegalStateException("beans are not registered.");
            }
            idList.remove(oldValue);
            idList.add((String) newValue);
        }
        // .
        if ("bindName".equalsIgnoreCase(notifyData.getKey())) {
            newValue = Objects.requireNonNull(newValue);
            BindInfo bindInfo = this.findBindInfo((String) newValue, target.getBindType());
            if (bindInfo != null) {
                throw new IllegalStateException("duplicate bind -> bindName '" + newValue + "' conflict with '" + bindInfo + "'");
            }
        }
        // .
        if ("bindType".equalsIgnoreCase(notifyData.getKey())) {
            throw new IllegalStateException("'bindType' are not allowed to be changed");
        }
    }

    /**
     * 遍历所有 BindInfo
     */
    public void forEach(Consumer<BindInfo<?>> action) {
        Objects.requireNonNull(action);
        for (BindInfo<?> t : this.allBindInfoList) {
            action.accept(t);
        }
    }

    /**
     * 初始化，把inited标记为true，从而锁住 createInfoAdapter 方法不在允许新对象注册进来。
     */
    protected void doInitialize() {
        // 相同类型，同名检测
        HashSet<String> names = new HashSet<>();
        this.indexTypeMapping.forEach((key, value) -> {
            value.forEach(bindID -> {
                BindInfo<?> bindInfo = idDataSource.get(bindID);
                String name = StringUtils.isBlank(bindInfo.getBindName()) ? null : bindInfo.getBindName();
                if (names.contains(name)) {
                    throw new IllegalStateException("conflict type '" + key + "' of same name '" + (StringUtils.isBlank(name) ? "'" : (" with name '" + name + "'")));
                } else {
                    names.add(name);
                }
            });
            names.clear();
        });
    }

    /**
     * 当容器停止运行时，需要做Bean清理工作。
     */
    @Override
    protected void doClose() {
        this.allBindInfoList.clear();
        this.indexTypeMapping.clear();
        this.idDataSource.clear();
    }
}