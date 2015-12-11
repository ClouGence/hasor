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
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.plugins.monitor.QpsMonitor;
import net.hasor.rsf.rpc.caller.RsfCaller;
import net.hasor.rsf.rpc.caller.SendData;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseInfo;
import test.net.hasor.rsf.services.EchoService;
/**
 * I5 Mac，测试结果，单机可以扛到QPS 15W+。
 * 
 * [Thread-3] INFO: count:6507294 , QPS:151332 , RT:1
 * [Thread-6] INFO: count:6810197 , QPS:154777 , RT:0
 * [Thread-5] INFO: count:7038688 , QPS:156415 , RT:0
 * [Thread-6] INFO: count:7304291 , QPS:158788 , RT:0
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
    private void runThread(final SerializeFactory factory, final RsfCaller caller, final Queue<RequestInfo> queue) {
        new Thread() {
            public void run() {
                while (true)
                    send(factory, caller, queue);
            };
        }.start();
    }
    //
    //
    //
    @Test
    public void callerTest() throws IOException, URISyntaxException, InterruptedException {
        final Queue<RequestInfo> queue = new LinkedBlockingQueue<RequestInfo>();
        //
        Module rsfModule = new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                final RsfSettings rsfSetting = new DefaultRsfSettings(apiBinder.getEnvironment().getSettings());//create RsfSettings
                RsfEnvironment rsfEnvironment = new DefaultRsfEnvironment(null, rsfSetting);//create RsfEnvironment
                RsfBeanContainer container = new RsfBeanContainer(rsfEnvironment);
                RsfContext rsfContext = new EmpytRsfContext() {
                    public RsfSettings getSettings() {
                        return rsfSetting;
                    }
                };
                //
                apiBinder.bindType(RsfSettings.class).toInstance(rsfSetting);
                apiBinder.bindType(RsfEnvironment.class).toInstance(rsfEnvironment);
                apiBinder.bindType(RsfBeanContainer.class).toInstance(container);
                apiBinder.bindType(RsfContext.class).toInstance(rsfContext);
            }
        };
        final AppContext appContext = Hasor.createAppContext(rsfModule);
        //
        //
        //Caller
        final RsfCaller caller = new RsfCaller(appContext, new SendData() {
            public void sendData(Provider<InterAddress> target, RequestInfo info) {
                queue.add(info);
            }
        });
        //
        //Request -> Response，然后再写回Caller。
        final SerializeFactory factory = SerializeFactory.createFactory(appContext.getInstance(RsfSettings.class));
        runThread(factory, caller, queue);
        runThread(factory, caller, queue);
        runThread(factory, caller, queue);
        runThread(factory, caller, queue);
        runThread(factory, caller, queue);
        runThread(factory, caller, queue);
        runThread(factory, caller, queue);
        //
        //RSF服务发布
        RsfBeanContainer container = appContext.getInstance(RsfBeanContainer.class);
        container.createBinder().bindFilter("QpsMonitor", new QpsMonitor());
        final RsfBindInfo<?> bindInfo = container.createBinder().rsfService(EchoService.class).timeout(30000).register();
        //
        //调用服务
        final Provider<InterAddress> target = new InstanceProvider<InterAddress>(new InterAddress("200.100.25.123", 8000, "unit"));
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
        for (int i = 0; i < 201; i++) {
            //发起四次调用，然后让这四个球在RSF容器里弹来弹去。
            caller.callBackInvoke(target, bindInfo, "sayHello", paramTypes, paramObjects, callBack);
        }
        //
        //
        Thread.sleep(240000);
    }
}