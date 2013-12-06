/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.plugins.aware.AppContextAware;
/**
 * 
 * @version : 2013-10-29
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class ServicesRegisterHandlerDefine implements AppContextAware {
    private Class<?>                serviceType;
    private Class<?>                handlerType;
    private ServicesRegisterHandler handler;
    // 
    public ServicesRegisterHandlerDefine(Class<?> serviceType, ServicesRegisterHandler handler) {
        Hasor.assertIsNotNull(serviceType, "serviceType is null.");
        Hasor.assertIsNotNull(handler, "ServicesRegisterHandler is null.");
        AwareUtil.registerAppContextAware(this);
        this.serviceType = serviceType;
        this.handlerType = handler.getClass();
        this.handler = handler;
    }
    public ServicesRegisterHandlerDefine(Class<?> serviceType, Class<ServicesRegisterHandler> handlerType) {
        Hasor.assertIsNotNull(serviceType, "serviceType is null.");
        Hasor.assertIsNotNull(handler, "ServicesRegisterHandler Type is null.");
        AwareUtil.registerAppContextAware(this);
        this.serviceType = serviceType;
        this.handlerType = handlerType;
    }
    public void setAppContext(AppContext appContext) {
        if (handler == null)
            this.handler = (ServicesRegisterHandler) appContext.getInstance(this.handlerType);
    }
    public Class<?> getServiceType() {
        return this.serviceType;
    }
    public boolean registerService(Object targetService) {
        return this.handler.registerService(targetService);
    }
    public boolean unRegisterService(Object targetService) {
        return this.handler.unRegisterService(targetService);
    }
    public ServicesRegisterHandler getHandler() {
        return this.handler;
    }
}