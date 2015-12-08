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
package net.hasor.rsf.rpc.caller;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.more.classcode.MoreClassLoader;
import org.more.future.FutureCallback;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.domain.RsfException;
/**
 * 
 * @version : 2015年12月8日
 * @author 赵永春(zyc@hasor.net)
 */
public class CallerManager extends AbstractCaller {
    private final RsfBeanContainer              rsfBeanContainer;
    private final Object                        LOCK_OBJECT;
    private final ConcurrentMap<String, Object> wrapperMap;
    //
    public CallerManager(RsfBeanContainer rsfBeanContainer, RsfContext rsfContext) {
        super(rsfContext);
        this.rsfBeanContainer = rsfBeanContainer;
        this.LOCK_OBJECT = new Object();
        this.wrapperMap = new ConcurrentHashMap<String, Object>();
    }
    public <T> T wrapper(InterAddress target, RsfBindInfo<?> bindInfo, Class<T> interFace) throws RsfException {
        if (bindInfo == null)
            throw new NullPointerException();
        if (interFace.isInterface() == false)
            throw new UnsupportedOperationException("interFace parameter must be an interFace.");
        //
        String bindID = bindInfo.getBindID();
        Object wrapperObject = this.wrapperMap.get(bindID);
        if (wrapperObject == null) {
            synchronized (LOCK_OBJECT) {
                wrapperObject = this.wrapperMap.get(bindID);
                if (wrapperObject == null) {
                    ClassLoader loader = new MoreClassLoader();
                    wrapperObject = Proxy.newProxyInstance(loader, new Class<?>[] { interFace }, new RemoteWrapper(bindInfo, this));
                    this.wrapperMap.put(bindID, wrapperObject);
                }
            }
        }
        return (T) wrapperObject;
        //
    }
    protected RsfFuture doSendRequest(RsfRequestFormLocal request, FutureCallback<RsfResponse> listener) {
        RsfClientRequestManager reqManager = this.rsfContext.getRequestManager();
        return reqManager.sendRequest(request, listener);
    }
}
class RemoteWrapper implements InvocationHandler {
    private RsfBindInfo<?> bindInfo = null;
    private RsfClient      client   = null;
    //
    public RemoteWrapper(RsfBindInfo<?> bindInfo, RsfClient client) {
        this.bindInfo = bindInfo;
        this.client = client;
    }
    public Object invoke(Object proxy, Method callMethod, Object[] args) throws Throwable {
        return this.client.syncInvoke(this.bindInfo, callMethod.getName(), callMethod.getParameterTypes(), args);
    }
}