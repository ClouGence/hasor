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
package test.net.hasor.rsf._03_address;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.junit.Test;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.address.AddressPool;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
/**
 * 
 * @version : 2015年12月2日
 * @author 赵永春(zyc@hasor.net)
 */
public class Address_AddressPoolTest extends AbstractAddressPoolTest {
    /*动态更新地址*/
    @Test
    public void dynamicAddress() throws IOException, URISyntaxException, InterruptedException {
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<--开始环境准备-->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //
        ConcurrentMap<String, ConcurrentMap<InterAddress, TimeData>> atomicMap = new ConcurrentHashMap<String, ConcurrentMap<InterAddress, TimeData>>();
        AppContext appContext = Hasor.createAppContext();
        RsfEnvironment rsfEnvironment = new DefaultRsfEnvironment(appContext.getEnvironment());//create RsfEnvironment
        AddressPool pool = new AddressPool(rsfEnvironment);//new AddressPool
        pool.startTimer();
        //
        List<InterAddress> addresses_1 = new ArrayList<InterAddress>();
        addresses_1.add(new InterAddress("192.168.137.10", 8000, "etc2"));//  rsf://192.168.137.10:8000/etc2
        addresses_1.add(new InterAddress("192.168.137.11", 8000, "etc2"));//  rsf://192.168.137.11:8000/etc2
        List<InterAddress> addresses_2 = new ArrayList<InterAddress>();
        addresses_2.add(new InterAddress("192.168.1.3", 8000, "etc3"));//     rsf://192.168.1.3:8000/etc3
        addresses_2.add(new InterAddress("192.168.1.4", 8000, "etc3"));//     rsf://192.168.1.4:8000/etc3
        addresses_2.add(new InterAddress("192.168.1.5", 8000, "etc3"));//     rsf://192.168.1.5:8000/etc3
        List<InterAddress> addresses_3 = new ArrayList<InterAddress>();
        addresses_3.add(new InterAddress("192.168.2.3", 8000, "etc1"));//     rsf://192.168.1.3:8000/etc1
        addresses_3.add(new InterAddress("192.168.3.4", 8000, "etc1"));//     rsf://192.168.1.4:8000/etc1
        addresses_3.add(new InterAddress("192.168.4.5", 8000, "etc1"));//     rsf://192.168.1.5:8000/etc1
        //
        String serviceID = "[RSF]test.net.hasor.rsf.services.EchoService-1.0.0";
        String methodName = "sayHello";
        String args = "say Hello";
        //
        //线程拼命的获取地址
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
        //动态新增服务
        pool.appendAddress(serviceID, addresses_1);
        Thread.sleep(10000);
        //
        //动态加入更多的地址
        pool.appendAddress(serviceID, addresses_2);
        pool.appendAddress(serviceID, addresses_3);
        Thread.sleep(10000);
        //
        //删除地址
        for (InterAddress addr : addresses_1)
            pool.removeAddress(serviceID, addr);
        Thread.sleep(10000);
        //
    }
    //
    /*动态失效地址和重新让地址生效。*/
    @Test
    public void invalidAddress() throws IOException, URISyntaxException, InterruptedException {
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<--开始环境准备-->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //
        ConcurrentMap<String, ConcurrentMap<InterAddress, TimeData>> atomicMap = new ConcurrentHashMap<String, ConcurrentMap<InterAddress, TimeData>>();
        AppContext appContext = Hasor.createAppContext("03_address-config.xml");
        RsfEnvironment rsfEnvironment = new DefaultRsfEnvironment(appContext.getEnvironment());//create RsfEnvironment
        AddressPool pool = new AddressPool(rsfEnvironment);//new AddressPool
        pool.startTimer();
        //
        List<InterAddress> addresses_1 = new ArrayList<InterAddress>();
        addresses_1.add(new InterAddress("192.168.137.10", 8000, "etc2"));//  rsf://192.168.137.10:8000/etc2
        addresses_1.add(new InterAddress("192.168.137.11", 8000, "etc2"));//  rsf://192.168.137.11:8000/etc2
        //
        String serviceID = "[RSF]test.net.hasor.rsf.services.EchoService-1.0.0";
        String methodName = "sayHello";
        String args = "say Hello";
        //
        //线程拼命的获取地址
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
        //Test - 0 ，动态新增服务，同时指定地址。
        pool.appendAddress(serviceID, addresses_1);
        Thread.sleep(10000);
        //
        //Test - 1 ，使一个地址失效。
        pool.invalidAddress(new InterAddress("192.168.137.10", 8000, "etc2"));
        Thread.sleep(10000);
        //
        //Test - 2 ，使另个地址失效。
        pool.invalidAddress(new InterAddress("192.168.137.11", 8000, "etc2"));
        Thread.sleep(10000);
        //
        //Test - 3 ，失效的地址重新激活。
        pool.appendAddress(serviceID, addresses_1);
        Thread.sleep(10000);
    }
}