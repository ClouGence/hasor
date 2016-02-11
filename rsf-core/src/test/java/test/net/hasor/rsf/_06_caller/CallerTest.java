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
package test.net.hasor.rsf._06_caller;
import java.io.IOException;
import java.net.URISyntaxException;
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
import net.hasor.rsf.domain.InstanceAddressProvider;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.plugins.filters.monitor.QpsMonitor;
import net.hasor.rsf.rpc.caller.RsfCaller;
import net.hasor.rsf.rpc.caller.SenderListener;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseInfo;
import test.net.hasor.rsf.services.EchoService;
/**
 * 请求发出。
 * 
 * I5 Mac，2C，16G。
 * 使用阻塞队列。经过参数调优，从调用发起到产生RequestInfo，单机可以轻松扛到 14W+ QPS。
 *  －QPS低下，主要性能压力在阻塞队列上，测试程序使用的是阻塞队列做为RequestInfo的承载容器。
 *  －OptionInfo.addOptionMap，也很消耗性能。
 * 
 * [Thread-7] INFO: count:16040540 , QPS:141951 , RT:0
 * [Thread-4] INFO: count:16480764 , QPS:144568 , RT:1
 * [Thread-7] INFO: count:16480764 , QPS:144568 , RT:1
 * [Thread-4] INFO: count:16894816 , QPS:146911 , RT:1
 * [Thread-5] INFO: count:16930141 , QPS:141084 , RT:0
 * [Thread-6] INFO: count:16930141 , QPS:141084 , RT:0
 * @version : 2015年12月9日
 * @author 赵永春(zyc@hasor.net)
 */
public class CallerTest {
    private void runThread(final RsfCaller caller, final Queue<RequestInfo> queue) {
        new Thread() {
            public void run() {
                ResponseInfo responseInfo = null;
                while (true) {
                    RequestInfo info = queue.poll();
                    if (info == null) {
                        continue;
                    }
                    if (responseInfo == null) {
                        responseInfo = new ResponseInfo();
                        responseInfo.setStatus(ProtocolStatus.OK);
                        responseInfo.setReturnData(info.getParameterValues().get(0));
                        responseInfo.setSerializeType(info.getSerializeType());
                    }
                    //
                    responseInfo.setReceiveTime(System.currentTimeMillis());
                    responseInfo.setRequestID(info.getRequestID());
                    caller.putResponse(responseInfo);
                }
            };
        }.start();
    }
    //
    @Test
    public void callerTest() throws IOException, URISyntaxException, InterruptedException {
        final Queue<RequestInfo> queue = new ConcurrentLinkedQueue<RequestInfo>();
        //
        AppContext appContext = Hasor.createAppContext("03_address-config.xml");
        final RsfEnvironment rsfEnvironment = new DefaultRsfEnvironment(appContext.getEnvironment());//create RsfEnvironment
        final RsfBeanContainer container = new RsfBeanContainer(rsfEnvironment);
        final RsfContext rsfContext = new EmpytRsfContext() {
            public RsfSettings getSettings() {
                return rsfEnvironment.getSettings();
            }
        };
        //
        //
        //
        //Caller
        final RsfCaller caller = new RsfCaller(rsfContext, container, new SenderListener() {
            public void sendRequest(Provider<InterAddress> target, RequestInfo info) {
                queue.add(info);
            }
        });
        //
        //Request -> Response，然后再写回Caller。
        for (int i = 0; i < 4; i++) {
            runThread(caller, queue);
        }
        //
        //RSF服务发布
        container.createBinder().bindFilter("QpsMonitor", new QpsMonitor());
        final RsfBindInfo<?> bindInfo = container.createBinder().rsfService(EchoService.class).timeout(30000).register();
        //
        //调用服务
        final InstanceAddressProvider target = new InstanceAddressProvider(new InterAddress("200.100.25.123", 8000, "unit"));
        final Class<?>[] paramTypes = new Class<?>[] { String.class };
        final Class<?>[] paramObjects = new Class<?>[] { String.class };
        FutureCallback<Object> callBack = new FutureCallback<Object>() {
            public void failed(Throwable ex) {
                System.out.println(ex.getClass() + ex.getMessage());
            }
            public void completed(Object arg0) {
                caller.callBackInvoke(target, bindInfo, "sayHello", paramTypes, paramObjects, this);
            }
            public void cancelled() {
                caller.callBackInvoke(target, bindInfo, "sayHello", paramTypes, paramObjects, this);
            }
        };
        for (int i = 0; i < 208; i++) {
            //发起四次调用，然后让这四个球在RSF容器里弹来弹去。
            caller.callBackInvoke(target, bindInfo, "sayHello", paramTypes, paramObjects, callBack);
        }
        //
        //
        Thread.sleep(240000);
    }
}