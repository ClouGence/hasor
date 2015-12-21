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
package test.net.hasor.rsf._06_caller;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.Test;
import org.more.future.FutureCallback;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.domain.AddressProvider;
import net.hasor.rsf.domain.InstanceAddressProvider;
import net.hasor.rsf.plugins.filters.monitor.QpsMonitor;
import net.hasor.rsf.rpc.caller.remote.RemoteRsfCaller;
import net.hasor.rsf.rpc.caller.remote.RemoteSenderListener;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseBlock;
import net.hasor.rsf.transform.protocol.ResponseInfo;
import test.net.hasor.rsf.services.EchoService;
import test.net.hasor.rsf.services.EchoServiceImpl;
/**
 * I5 Mac，2C，16G。
 * 通过真实模拟RSF请求和响应。每秒QPS可以达到 8.7W+。
 *  －QPS低下，主要性能压力在阻塞队列上，测试程序使用的是阻塞队列做为RequestInfo的承载容器。
 *  －OptionInfo.addOptionMap，也很消耗性能。
 *  －OptionInfo.addOption，也很消耗性能。
 * 
 * @version : 2015年12月9日
 * @author 赵永春(zyc@hasor.net)
 */
public class RemoteCallerTest implements RemoteSenderListener {
    private Queue<RequestInfo> queue  = new ConcurrentLinkedQueue<RequestInfo>();
    private RemoteRsfCaller    client = null;
    private RemoteRsfCaller    server = null;
    //
    //
    //
    public void sendRequest(Provider<InterAddress> target, RequestInfo info) {
        this.queue.add(info);
    }
    public void sendResponse(InterAddress target, ResponseInfo info) {
        info.setReceiveTime(System.currentTimeMillis());
        client.putResponse(info);
    }
    public void sendResponse(InterAddress target, ResponseBlock block) {
        System.err.println(block.getRequestID() + " , status=" + block.getStatus());
    }
    //
    //
    //
    private void runThread(final RemoteRsfCaller client, final RemoteRsfCaller server) {
        new Thread() {
            public void run() {
                while (true) {
                    RequestInfo info = queue.poll();
                    if (info == null) {
                        continue;
                    }
                    info.setReceiveTime(System.currentTimeMillis());
                    server.onRequest(null, info);
                }
            };
        }.start();
    }
    //
    //
    //
    @Test
    public void callerTest() throws Throwable {
        //Caller
        this.client = createRemoteRsfCaller();
        this.server = createRemoteRsfCaller();
        //
        //Request -> Response，然后再写回Caller。
        for (int i = 0; i < 4; i++) {
            runThread(client, server);
        }
        //
        //RSF服务发布
        RsfBeanContainer containerServer = this.server.getContainer();
        containerServer.createBinder().rsfService(EchoService.class).toInstance(new EchoServiceImpl()).register();
        //
        RsfBeanContainer containerClient = this.client.getContainer();
        containerClient.createBinder().bindFilter("QpsMonitor", new QpsMonitor());
        final RsfBindInfo<?> bindInfo = containerClient.createBinder().rsfService(EchoService.class).register();
        //
        //
        //
        //调用服务
        final AddressProvider target = new InstanceAddressProvider(new InterAddress("200.100.25.123", 8000, "unit"));
        final Class<?>[] paramTypes = new Class<?>[] { String.class };
        final Object[] paramObjects = new Object[] { "hello word" };
        FutureCallback<Object> callBack = new FutureCallback<Object>() {
            public void failed(Throwable ex) {
                System.out.println(ex.getClass() + ex.getMessage());
            }
            public void completed(Object arg0) {
                client.callBackInvoke(target, bindInfo, "sayHello", paramTypes, paramObjects, this);
            }
            public void cancelled() {
                client.callBackInvoke(target, bindInfo, "sayHello", paramTypes, paramObjects, this);
            }
        };
        for (int i = 0; i < 208; i++) {
            //发起四次调用，然后让这四个球在RSF容器里弹来弹去。
            client.callBackInvoke(target, bindInfo, "sayHello", paramTypes, paramObjects, callBack);
        }
        //
        //
        Thread.sleep(240000);
    }
    private RemoteRsfCaller createRemoteRsfCaller() throws Throwable {
        final AppContext appContext = Hasor.createAppContext();
        final RsfEnvironment rsfEnvironment = new DefaultRsfEnvironment(appContext.getEnvironment());//create RsfEnvironment
        final RsfBeanContainer container = new RsfBeanContainer(rsfEnvironment);
        final RsfContext rsfContext = new EmpytRsfContext() {
            public RsfSettings getSettings() {
                return rsfEnvironment.getSettings();
            }
            public <T> Provider<T> getServiceProvider(RsfBindInfo<T> bindInfo) {
                return (Provider<T>) container.getProvider(bindInfo.getBindID());
            }
        };
        return new RemoteRsfCaller(rsfContext, container, this);
    }
}