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
package net.hasor.rsf.runtime.client;
import java.lang.reflect.Method;
import net.hasor.rsf.metadata.ServiceMetaData;
import org.more.classcode.delegate.faces.MethodDelegate;
/**
 * 负责将接口的调用转发到client的同步调用方法上。
 * @version : 2014年9月19日
 * @author 赵永春(zyc@hasor.net)
 */
class RemoteWrapper implements MethodDelegate {
    private ServiceMetaData<?> service = null;
    private RsfClient          client  = null;
    //
    public RemoteWrapper(ServiceMetaData<?> service, RsfClient client) {
        this.service = service;
        this.client = client;
    }
    public Object invoke(Method callMethod, Object target, Object[] params) throws Throwable {
        return this.client.syncInvoke(this.service, callMethod.getName(), callMethod.getParameterTypes(), params);
    }
}