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
package test.net.hasor.rsf._05_container;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.junit.Test;
import net.hasor.core.Settings;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfBinder.RegisterBuilder;
import net.hasor.rsf.RsfBinder.RegisterReference;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.AddressPool;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
import test.net.hasor.rsf._03_address.AbstractAddressPoolTest;
import test.net.hasor.rsf.services.EchoService;
/**
 * 
 * @version : 2015年12月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServiceContainerTest extends AbstractAddressPoolTest {
    @Test
    public void dynamicService() throws Throwable {
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<--开始环境准备-->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //
        ConcurrentMap<String, ConcurrentMap<InterAddress, TimeData>> atomicMap = new ConcurrentHashMap<String, ConcurrentMap<InterAddress, TimeData>>();
        Settings setting = new StandardContextSettings();//create Settings
        RsfSettings rsfSetting = new DefaultRsfSettings(setting);//create RsfSettings
        RsfEnvironment rsfEnvironment = new DefaultRsfEnvironment(null, rsfSetting);//create RsfEnvironment
        RsfBeanContainer container = new RsfBeanContainer(rsfEnvironment);
        container.getAddressPool().startTimer();
        //
        InterAddress[] addresses_1 = new InterAddress[2];
        addresses_1[0] = new InterAddress("192.168.137.10", 8000, "etc2");//  rsf://192.168.137.10:8000/etc2
        addresses_1[1] = new InterAddress("192.168.137.11", 8000, "etc2");//  rsf://192.168.137.11:8000/etc2
        InterAddress[] addresses_2 = new InterAddress[3];
        addresses_2[0] = new InterAddress("192.168.1.3", 8000, "etc3");//     rsf://192.168.1.3:8000/etc3
        addresses_2[1] = new InterAddress("192.168.1.4", 8000, "etc3");//     rsf://192.168.1.4:8000/etc3
        addresses_2[2] = new InterAddress("192.168.1.5", 8000, "etc3");//     rsf://192.168.1.5:8000/etc3
        //
        RsfBinder rsfBinder = container.createBinder();
        RegisterBuilder<?> builder = rsfBinder.rsfService(EchoService.class).bindAddress(null, addresses_1).bindAddress(null, addresses_2);
        RegisterReference<?> ref = builder.register();
        String serviceID = "[RSF]test.net.hasor.rsf.services.EchoService-1.0.0";
        String methodName = "sayHello";
        String args = "say Hello";
        //
        //线程拼命的获取地址
        AddressPool pool = container.getAddressPool();
        Thread workThread_1 = new Thread(new NextWork(serviceID, methodName, args, pool, atomicMap), "WorkThread_1");
        Thread workThread_2 = new Thread(new NextWork(serviceID, methodName, args, pool, atomicMap), "WorkThread_2");
        Thread workThread_3 = new Thread(new NextWork(serviceID, methodName, args, pool, atomicMap), "WorkThread_3");
        Thread workThread_4 = new Thread(new NextWork(serviceID, methodName, args, pool, atomicMap), "WorkThread_4");
        Thread monitorThread = new Thread(new MonitorWork(atomicMap), "MonitorThread");
        workThread_1.start();
        workThread_2.start();
        workThread_3.start();
        workThread_4.start();
        monitorThread.start();
        Thread.sleep(5000);
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<--环境准备完毕-->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //
        //删除地址
        for (InterAddress addr : addresses_1)
            pool.removeAddress(serviceID, addr);
        Thread.sleep(10000);
        //
        System.out.println("unRegister");
        ref.unRegister();
        Thread.sleep(10000);
        //
    }
}