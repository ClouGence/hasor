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
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.Test;
import org.more.future.FutureCallback;
import net.hasor.core.Provider;
import net.hasor.core.Settings;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.plugins.monitor.QpsMonitor;
import net.hasor.rsf.rpc.caller.RsfCaller;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseInfo;
import test.net.hasor.rsf.services.EchoService;
/**
 * 
 * @version : 2015年12月9日
 * @author 赵永春(zyc@hasor.net)
 */
public class CallerTest {
    private void send(SerializeFactory factory, final RsfCaller caller, Queue<RequestInfo> queue) {
        try {
            RequestInfo info = queue.poll();
            if (info == null) {
                return;
            }
            byte[] inParam = info.getParameterValues().get(0);
            //
            ResponseInfo responseInfo = new ResponseInfo();
            responseInfo.setReceiveTime(System.currentTimeMillis());
            responseInfo.setRequestID(info.getRequestID());
            responseInfo.setStatus(ProtocolStatus.OK);
            responseInfo.setReturnData(inParam);
            responseInfo.setSerializeType(info.getSerializeType());
            caller.putResponse(responseInfo);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    @Test
    public void callerTest() throws IOException, URISyntaxException, InterruptedException {
        //准备环境
        final Settings setting = new StandardContextSettings();//create Settings
        final RsfSettings rsfSetting = new DefaultRsfSettings(setting);//create RsfSettings
        final RsfEnvironment rsfEnvironment = new DefaultRsfEnvironment(null, rsfSetting);//create RsfEnvironment
        final RsfBeanContainer container = new RsfBeanContainer(rsfEnvironment);
        final SerializeFactory factory = SerializeFactory.createFactory(rsfSetting);
        final Queue<RequestInfo> queue = new LinkedBlockingQueue<RequestInfo>();
        final RsfContext emptyRsfContext = new EmpytRsfContext() {
            public RsfSettings getSettings() {
                return rsfEnvironment.getSettings();
            }
        };
        //创建Caller
        final RsfCaller caller = new RsfCaller(container, emptyRsfContext) {
            protected void sendData(Provider<InterAddress> target, final RequestInfo info) {
                queue.add(info);
            }
        };
        //
        new Thread() {
            public void run() {
                while (true) {
                    send(factory, caller, queue);
                }
            };
        }.start();
        new Thread() {
            public void run() {
                while (true) {
                    send(factory, caller, queue);
                }
            };
        }.start();
        new Thread() {
            public void run() {
                while (true) {
                    send(factory, caller, queue);
                }
            };
        }.start();
        new Thread() {
            public void run() {
                while (true) {
                    send(factory, caller, queue);
                }
            };
        }.start();
        new Thread() {
            public void run() {
                while (true) {
                    send(factory, caller, queue);
                }
            };
        }.start();
        new Thread() {
            public void run() {
                while (true) {
                    send(factory, caller, queue);
                }
            };
        }.start();
        //
        //注册服务
        container.createBinder().bindFilter("QpsMonitor", new QpsMonitor());
        final RsfBindInfo<?> bindInfo = container.createBinder().rsfService(EchoService.class).timeout(30000).register();
        final Provider<InterAddress> target = new InstanceProvider<InterAddress>(new InterAddress("200.100.25.123", 8000, "unit"));
        final Class<?>[] paramTypes = new Class<?>[] { String.class };
        final Class<?>[] paramObjects = new Class<?>[] { String.class };
        //
        //异步调用服务
        //for (int i = 0; i < 10; i++) {
        //String serviceID = bindInfo.getBindID();
        //EchoService echo = (EchoService) caller.getRemoteByID(target, serviceID);
        FutureCallback<Object> callBack = new FutureCallback<Object>() {
            @Override
            public void failed(Throwable ex) {
                caller.callBackInvoke(target, bindInfo, "sayHello", paramTypes, paramObjects, this);
            }
            @Override
            public void completed(Object arg0) {
                caller.callBackInvoke(target, bindInfo, "sayHello", paramTypes, paramObjects, this);
            }
            @Override
            public void cancelled() {
                // TODO Auto-generated method stub
            }
        };
        caller.callBackInvoke(target, bindInfo, "sayHello", paramTypes, paramObjects, callBack);
        //}
        //
        //System.out.println();
        Thread.sleep(240000);
    }
}