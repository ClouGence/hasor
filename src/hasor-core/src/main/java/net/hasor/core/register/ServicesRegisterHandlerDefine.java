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
/**
 * 
 * @version : 2013-10-29
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class ServicesRegisterHandlerDefine<T> {
    private Class<T>                   serviceType;
    private ServicesRegisterHandler<T> handler;
    // 
    public ServicesRegisterHandlerDefine(Class<T> serviceType, ServicesRegisterHandler<T> handler) {
        this.serviceType = serviceType;
        this.handler = handler;
    }
    public Class<?> getServiceType() {
        return this.serviceType;
    }
    public void registerService(T targetService) {
        this.handler.registerService(targetService);
    }
    public void unRegisterService(T targetService) {
        this.handler.unRegisterService(targetService);
    }
}