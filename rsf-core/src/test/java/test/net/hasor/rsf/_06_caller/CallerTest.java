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
import java.util.List;
import org.junit.Test;
import net.hasor.core.Provider;
import net.hasor.core.Settings;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.rpc.caller.RsfCaller;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.serialize.coder.JsonSerializeCoder;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseInfo;
import test.net.hasor.rsf.services.EchoService;
/**
 * 
 * @version : 2015年12月9日
 * @author 赵永春(zyc@hasor.net)
 */
public class CallerTest {
    private void send(SerializeFactory factory, final RsfCaller caller, final RequestInfo info) {
        try {
            byte[] inParam = info.getParameterValues().get(0);
            Object inObject = factory.getSerializeCoder(info.getSerializeType()).decode(inParam);
            //
            JsonSerializeCoder coder = new JsonSerializeCoder();
            ResponseInfo responseInfo = new ResponseInfo();
            responseInfo.setReceiveTime(System.currentTimeMillis());
            responseInfo.setRequestID(info.getRequestID());
            responseInfo.setStatus(ProtocolStatus.OK);
            responseInfo.setReturnData(coder.encode("echo " + inObject));
            responseInfo.setSerializeType("json");
            caller.putResponse(responseInfo);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    @Test
    public void callerTest() throws IOException, URISyntaxException {
        //
        //准备环境
        final Settings setting = new StandardContextSettings();//create Settings
        final RsfSettings rsfSetting = new DefaultRsfSettings(setting);//create RsfSettings
        final RsfEnvironment rsfEnvironment = new DefaultRsfEnvironment(null, rsfSetting);//create RsfEnvironment
        final RsfBeanContainer container = new RsfBeanContainer(rsfEnvironment);
        final SerializeFactory factory = SerializeFactory.createFactory(rsfSetting);
        final RsfContext emptyRsfContext = new DefaultContext() {
            public RsfSettings getSettings() {
                return rsfEnvironment.getSettings();
            }
        };
        //
        //创建Caller
        final RsfCaller caller = new RsfCaller(container, emptyRsfContext) {
            protected void sendData(Provider<InterAddress> target, final RequestInfo info) {
                //                System.out.println("do send Request(" + i`nfo.getRequestID() + "). to " + target.get());
                final RsfCaller caller = this;
                new Thread() {
                    public void run() {
                        send(factory, caller, info);
                    };
                }.start();
            }
        };
        //
        //注册服务
        Provider<InterAddress> target = new InstanceProvider<InterAddress>(new InterAddress("200.100.25.123", 8000, "unit"));
        RsfBindInfo<?> info = container.createBinder().rsfService(EchoService.class).timeout(30000).register();
        String serviceID = info.getBindID();
        EchoService echo = (EchoService) caller.getRemoteByID(target, serviceID);
        //
        //同步调用服务
        for (int i = 0; i < 10; i++) {
            try {
                long startTime = System.currentTimeMillis();
                String str = echo.sayHello("hello.");
                long t = System.currentTimeMillis() - startTime;
                System.out.println(t + "\t" + str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //
        //        System.out.println();
    }
}
class DefaultContext implements RsfContext {
    @Override
    public void shutdown() {
        // TODO Auto-generated method stub
    }
    @Override
    public RsfSettings getSettings() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public RsfClient getRsfClient() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T> T getBean(RsfBindInfo<T> bindInfo) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T> Provider<T> getServiceProvider(RsfBindInfo<T> bindInfo) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T> RsfBindInfo<T> getServiceInfo(String serviceID) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T> RsfBindInfo<T> getServiceInfo(Class<T> serviceType) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T> RsfBindInfo<T> getServiceInfo(String group, String name, String version) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public List<String> getServiceIDs() {
        // TODO Auto-generated method stub
        return null;
    }
}