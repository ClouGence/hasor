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
package net.hasor.core.register;
import static net.hasor.core.AppContext.ContextEvent_Start;
import static net.hasor.core.AppContext.ContextEvent_Stoped;
import static net.hasor.core.AppContext.ModuleEvent_Start;
import static net.hasor.core.AppContext.ModuleEvent_Stoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hasor.Hasor;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.EventManager;
import net.hasor.core.module.ModulePropxy;
import com.google.inject.Key;
/**
 * 
 * @version : 2013-10-29
 * @author 赵永春(zyc@hasor.net)
 */
public class ServicesRegisterManager implements EventListener {
    private AppContext                                   appContext;
    private Map<Class<?>, ServicesRegisterHandlerDefine> handlerDefine;
    //
    public ServicesRegisterManager(final AppContext appContext) {
        this.appContext = appContext;
        this.handlerDefine = new HashMap<Class<?>, ServicesRegisterHandlerDefine>();
        EventManager eventMsg = this.appContext.getEventManager();
        eventMsg.addEventListener(ContextEvent_Start, this);
        eventMsg.addEventListener(ContextEvent_Stoped, this);
        eventMsg.addEventListener(ModuleEvent_Start, this);
        eventMsg.addEventListener(ModuleEvent_Stoped, this);
        eventMsg.pushEventListener(ContextEvent_Start, new EventListener() {
            public void onEvent(String event, Object[] params) throws Throwable {
                List<ServicesRegisterHandlerDefine> defineList = appContext.findBeanByType(ServicesRegisterHandlerDefine.class);
                if (defineList == null)
                    return;
                for (ServicesRegisterHandlerDefine handler : defineList)
                    handlerDefine.put(handler.getServiceType(), handler);
            }
        });
    }
    /***/
    public void onEvent(String event, Object[] params) throws Throwable {
        /*  */if (event.equals(ContextEvent_Start)) {
            /*补充，注册服务*/
        } else if (event.equals(ContextEvent_Stoped)) {
            /*补充，解除注册*/
        } else if (event.equals(ModuleEvent_Start)) {
            /*补充，注册服务*/
        } else if (event.equals(ModuleEvent_Stoped)) {
            /*补充，解除注册*/
        }
    }
    //
    //    private Map<Object, List<MappingItem>> serviceGroup = new HashMap<Object, List<MappingItem>>();
    //    private List<MappingItem> getServiceList(Object groupBy) {
    //        List<MappingItem> serviceList = this.serviceGroup.get(groupBy);
    //        if (serviceList == null) {
    //            serviceList = new ArrayList<MappingItem>();
    //            this.serviceGroup.put(groupBy, serviceList);
    //        }
    //        return serviceList;
    //    }
    //    private Object getCurrentGroupKey() {
    //        Object currentModule = ModulePropxy.getLocalModuleInfo(appContext);
    //        currentModule = (currentModule == null) ? appContext : currentModule;
    //        return currentModule;
    //    }
    //
    //
    private static class MappingItem {
        public Class<?> type   = null; //服务类型
        public Object   target = null; //服务
        public Object   source = null; //来源
    }
    private MappingItem createMappingItem(Class<?> type, Object target) {
        MappingItem e = new MappingItem();
        e.type = type;
        e.target = target;
        e.source = ModulePropxy.getLocalModuleInfo(this.appContext);
        return e;
    }
    private Map<Object, MappingItem> serviceBeanMapping = new HashMap<Object, MappingItem>();
    /*注册服务。*/
    public synchronized boolean registerService(Class<?> type, Class<?> serviceType, Object... objects) {
        Hasor.assertIsNotNull(type, "the binding type of Service is null.");
        Hasor.assertIsNotNull(serviceType, "serviceType is null.");
        Hasor.assertIsLegal(!serviceBeanMapping.containsKey(serviceType), "Repeat service registry at : " + serviceType);
        //
        Object serviceBean = this.appContext.getInstance(serviceType);
        MappingItem regItem = createMappingItem(type, serviceBean);
        boolean res = this._registerServiceObject(type, regItem, objects);
        if (res)
            this.serviceBeanMapping.put(serviceType, regItem);
        return res;
    };
    /*注册服务。*/
    public synchronized boolean registerService(Class<?> type, Key<?> serviceKey, Object... objects) {
        Hasor.assertIsNotNull(type, "the binding type of Service is null.");
        Hasor.assertIsNotNull(serviceKey, "serviceKey is null.");
        Hasor.assertIsLegal(!serviceBeanMapping.containsKey(serviceKey), "Repeat service registry at : " + serviceKey);
        //
        Object serviceBean = this.appContext.getGuice().getInstance(serviceKey);
        MappingItem regItem = createMappingItem(type, serviceBean);
        boolean res = this._registerServiceObject(type, regItem, objects);
        if (res)
            this.serviceBeanMapping.put(serviceKey, regItem);
        return res;
    };
    /*注册服务。*/
    public synchronized boolean registerServiceObject(Class<?> type, Object serviceBean, Object... objects) {
        Hasor.assertIsNotNull(type, "the binding type of Service is null.");
        Hasor.assertIsNotNull(serviceBean, "serviceBean is null.");
        Hasor.assertIsLegal(!serviceBeanMapping.containsKey(serviceBean), "Repeat service registry at : " + serviceBean);
        //
        MappingItem regItem = createMappingItem(type, serviceBean);
        boolean res = this._registerServiceObject(type, regItem, objects);
        if (res)
            this.serviceBeanMapping.put(serviceBean, regItem);
        return res;
    };
    /*解除注册服务。*/
    public synchronized boolean unRegisterService(Class<?> type, Class<?> serviceType) {
        Hasor.assertIsNotNull(type, "the binding type of Service is null.");
        Hasor.assertIsNotNull(serviceType, "serviceType is null.");
        //
        if (!serviceBeanMapping.containsKey(serviceType))
            return false;
        boolean res = this._unRegisterServiceObject(type, serviceBeanMapping.get(serviceType));
        if (res)
            serviceBeanMapping.remove(serviceType);
        return res;
    };
    /*解除注册服务。*/
    public synchronized boolean unRegisterService(Class<?> type, Key<?> serviceKey) {
        Hasor.assertIsNotNull(type, "the binding type of Service is null.");
        Hasor.assertIsNotNull(serviceKey, "serviceKey is null.");
        //
        if (!serviceBeanMapping.containsKey(serviceKey))
            return false;
        boolean res = this._unRegisterServiceObject(type, serviceBeanMapping.get(serviceKey));
        if (res)
            serviceBeanMapping.remove(serviceKey);
        return res;
    };
    /*解除注册服务。*/
    public synchronized boolean unRegisterServiceObject(Class<?> type, Object serviceBean) {
        Hasor.assertIsNotNull(type, "the binding type of Service is null.");
        Hasor.assertIsNotNull(serviceBean, "serviceBean is null.");
        //
        if (!serviceBeanMapping.containsKey(serviceBean))
            return false;
        boolean res = this._unRegisterServiceObject(type, serviceBeanMapping.get(serviceBean));
        if (res)
            serviceBeanMapping.remove(serviceBean);
        return res;
    };
    /*注册服务。*/
    private boolean _registerServiceObject(Class<?> serviceType, MappingItem serviceBean, Object... objects) throws ServicesRegisterException {
        ServicesRegisterHandlerDefine define = this.handlerDefine.get(serviceType);
        if (define == null)
            throw new ServicesRegisterException("undefined ServicesRegisterHandler of type " + serviceType.getName());
        return define.registerService(serviceBean.target);
    };
    /*解除注册服务。*/
    private boolean _unRegisterServiceObject(Class<?> serviceType, MappingItem serviceBean) throws ServicesRegisterException {
        ServicesRegisterHandlerDefine define = this.handlerDefine.get(serviceType);
        if (define == null)
            throw new ServicesRegisterException("undefined ServicesRegisterHandler of type " + serviceType.getName());
        return define.unRegisterService(serviceBean.target);
    }
    public ServicesRegisterHandler lookUpRegisterService(Class<?> type) {
        ServicesRegisterHandlerDefine define = this.handlerDefine.get(type);
        if (define != null)
            return define.getHandler();
        return null;
    };
}