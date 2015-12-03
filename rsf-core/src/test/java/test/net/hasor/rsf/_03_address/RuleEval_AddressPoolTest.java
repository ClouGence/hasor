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
package test.net.hasor.rsf._03_address;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.junit.Test;
import org.more.util.ResourcesUtils;
import org.more.util.io.IOUtils;
import net.hasor.core.Settings;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.AddressPool;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.address.RouteTypeEnum;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
/**
 * 
 * @version : 2015年12月2日
 * @author 赵永春(zyc@hasor.net)
 */
public class RuleEval_AddressPoolTest extends AbstractAddressPoolTest {
    //
    /*动态更新服务级路由规则。
     * -- 路由规则是只有etc2机房的地址才会被调用。*/
    @Test
    public void serviceRuleAddress() throws IOException, URISyntaxException, InterruptedException {
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<--开始环境准备-->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //
        ConcurrentMap<String, ConcurrentMap<InterAddress, TimeData>> atomicMap = new ConcurrentHashMap<String, ConcurrentMap<InterAddress, TimeData>>();
        Settings setting = new StandardContextSettings();//create Settings
        RsfSettings rsfSetting = new DefaultRsfSettings(setting);//create RsfSettings
        RsfEnvironment rsfEnvironment = new DefaultRsfEnvironment(null, rsfSetting);//create RsfEnvironment
        AddressPool pool = new AddressPool(rsfEnvironment);//new AddressPool
        pool.init();
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
        //给服务分配地址
        System.out.println("-> updateAddress.");
        pool.updateAddress(serviceID, addresses_1);
        pool.updateAddress(serviceID, addresses_2);
        pool.updateAddress(serviceID, addresses_3);
        Thread.sleep(10000);
        //
        //服务级路由规则，应用规则之后只有etc2机房的地址可用。
        System.out.println("-> updateDefaultRoute.");
        String script = IOUtils.toString(ResourcesUtils.getResourceAsStream("/rule-script/service-level.groovy"));
        pool.updateDefaultRoute(RouteTypeEnum.ServiceLevel, script);
        Thread.sleep(10000);
    }
    //
    /*动态更新方法级路由规则。
     * --路由规则是sayEcho使用etc2机房，testUserTag使用etc1机房。*/
    @Test
    public void methodRuleAddress() throws IOException, URISyntaxException, InterruptedException {
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<--开始环境准备-->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //
        ConcurrentMap<String, ConcurrentMap<InterAddress, TimeData>> atomicMap = new ConcurrentHashMap<String, ConcurrentMap<InterAddress, TimeData>>();
        Settings setting = new StandardContextSettings();//create Settings
        RsfSettings rsfSetting = new DefaultRsfSettings(setting);//create RsfSettings
        RsfEnvironment rsfEnvironment = new DefaultRsfEnvironment(null, rsfSetting);//create RsfEnvironment
        AddressPool pool = new AddressPool(rsfEnvironment);//new AddressPool
        pool.init();
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
        String methodName1 = "sayEcho";
        String methodName2 = "testUserTag";
        String args = "say Hello";
        //
        //线程拼命的获取地址
        Thread workThread_1 = new Thread(new NextWork(serviceID, methodName1, args, pool, atomicMap), "WorkThread_1");
        Thread workThread_2 = new Thread(new NextWork(serviceID, methodName1, args, pool, atomicMap), "WorkThread_2");
        Thread workThread_3 = new Thread(new NextWork(serviceID, methodName2, args, pool, atomicMap), "WorkThread_3");
        Thread workThread_4 = new Thread(new NextWork(serviceID, methodName2, args, pool, atomicMap), "WorkThread_4");
        Thread monitorThread = new Thread(new MonitorWork(atomicMap), "MonitorThread");
        workThread_1.start();
        workThread_2.start();
        workThread_3.start();
        workThread_4.start();
        monitorThread.start();
        Thread.sleep(5000);
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<--环境准备完毕-->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //
        //给服务分配地址
        System.out.println("-> updateAddress.");
        pool.updateAddress(serviceID, addresses_1);
        pool.updateAddress(serviceID, addresses_2);
        pool.updateAddress(serviceID, addresses_3);
        Thread.sleep(10000);
        //
        //服务级路由规则，应用规则之后sayEcho使用etc2机房，testUserTag使用etc1机房。
        System.out.println("-> updateDefaultRoute.");
        String script = IOUtils.toString(ResourcesUtils.getResourceAsStream("/rule-script/method-level.groovy"));
        pool.updateDefaultRoute(RouteTypeEnum.MethodLevel, script);
        Thread.sleep(10000);
    }
    //
    /*动态更新方法级路由规则。
     * --路由规则是sayEcho使用192.168.137.10地址，testUserTag使用192.168.1.3-4。*/
    @Test
    public void argsRuleAddress() throws IOException, URISyntaxException, InterruptedException {
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<--开始环境准备-->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //
        ConcurrentMap<String, ConcurrentMap<InterAddress, TimeData>> atomicMap = new ConcurrentHashMap<String, ConcurrentMap<InterAddress, TimeData>>();
        Settings setting = new StandardContextSettings("03_args-config.xml");//create Settings
        RsfSettings rsfSetting = new DefaultRsfSettings(setting);//create RsfSettings
        RsfEnvironment rsfEnvironment = new DefaultRsfEnvironment(null, rsfSetting);//create RsfEnvironment
        AddressPool pool = new AddressPool(rsfEnvironment);//new AddressPool
        pool.init();
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
        String methodName1 = "sayEcho";
        String methodName2 = "testUserTag";
        String args1 = "sayTo_etc1";
        String args2 = "sayTo_etc2";
        String args3 = "server_3";
        String args4 = "server_4";
        //
        //线程拼命的获取地址
        Thread workThread_1 = new Thread(new NextWork(serviceID, methodName1, args1, pool, atomicMap), "WorkThread_1");
        Thread workThread_2 = new Thread(new NextWork(serviceID, methodName1, args2, pool, atomicMap), "WorkThread_2");
        Thread workThread_3 = new Thread(new NextWork(serviceID, methodName2, args3, pool, atomicMap), "WorkThread_3");
        Thread workThread_4 = new Thread(new NextWork(serviceID, methodName2, args4, pool, atomicMap), "WorkThread_4");
        Thread monitorThread = new Thread(new MonitorWork(atomicMap), "MonitorThread");
        workThread_1.start();
        workThread_2.start();
        workThread_3.start();
        workThread_4.start();
        monitorThread.start();
        Thread.sleep(5000);
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<--环境准备完毕-->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //
        //给服务分配地址
        System.out.println("-> updateAddress.");
        pool.updateAddress(serviceID, addresses_1);
        pool.updateAddress(serviceID, addresses_2);
        pool.updateAddress(serviceID, addresses_3);
        Thread.sleep(10000);
        //
        //服务级路由规则，应用规则之后sayEcho使用etc2机房，testUserTag使用etc1机房。
        System.out.println("-> updateDefaultRoute.");
        String script = IOUtils.toString(ResourcesUtils.getResourceAsStream("/rule-script/args-level.groovy"));
        pool.updateDefaultRoute(RouteTypeEnum.ArgsLevel, script);
        Thread.sleep(100000);
    }
}