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
package test.net.hasor.rsf.functions;
import net.hasor.core.Hasor;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.address.AddressBucket;
import net.hasor.rsf.address.AddressPool;
import net.hasor.rsf.address.DiskCacheAddressPool;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.utils.IOUtils;
import net.hasor.rsf.utils.ResourcesUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class AddressPoolTest {
    protected void configService(AddressPool pool, String service) throws URISyntaxException, IOException {
        ArrayList<InterAddress> dynamicList = new ArrayList<InterAddress>();
        dynamicList.add(new InterAddress("127.0.0.1", 8000, "etc2"));
        dynamicList.add(new InterAddress("127.0.0.2", 8000, "etc2"));
        dynamicList.add(new InterAddress("127.0.0.3", 8000, "etc2"));
        dynamicList.add(new InterAddress("127.0.0.4", 8000, "etc2"));
        pool.appendAddress(service, dynamicList);
        //
        ArrayList<InterAddress> staticList = new ArrayList<InterAddress>();
        staticList.add(new InterAddress("127.0.1.1", 8000, "etc2"));
        staticList.add(new InterAddress("127.0.2.2", 8000, "etc2"));
        staticList.add(new InterAddress("127.0.3.3", 8000, "etc2"));
        staticList.add(new InterAddress("127.0.4.4", 8000, "etc2"));
        pool.appendStaticAddress(service, staticList);
        //
        String flowBody = IOUtils.readToString(ResourcesUtils.getResourceAsStream("/flow-control/full-flow.xml"));
        pool.updateFlowControl(service, flowBody);
        //
        String scriptBody1 = IOUtils.readToString(ResourcesUtils.getResourceAsStream("/rule-script/service-level.groovy"));
        pool.updateServiceRoute(service, scriptBody1);
        //
        String scriptBody2 = IOUtils.readToString(ResourcesUtils.getResourceAsStream("/rule-script/method-level.groovy"));
        pool.updateMethodRoute(service, scriptBody2);
        //
        String scriptBody3 = IOUtils.readToString(ResourcesUtils.getResourceAsStream("/rule-script/args-level.groovy"));
        pool.updateArgsRoute(service, scriptBody3);
    }
    //
    @Test
    public void saveToZipTest() throws URISyntaxException, IOException {
        DefaultRsfEnvironment rsfEnv = new DefaultRsfEnvironment(Hasor.createAppContext().getEnvironment());
        AddressPool pool = new AddressPool(rsfEnv);
        String serviceID = "HelloWord_";
        //
        for (int i = 0; i < 10; i++) {
            String service = serviceID + i;
            configService(pool, service);
        }
        //
        File outFile = new File(rsfEnv.getPluginDir(AddressPoolTest.class), "pool.zip");
        outFile.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(outFile, false);
        pool.storeConfig(out);
        out.flush();
        out.close();
    }
    @Test
    public void readFormZipTest() throws IOException, URISyntaxException {
        this.saveToZipTest();
        //
        DefaultRsfEnvironment rsfEnv = new DefaultRsfEnvironment(Hasor.createAppContext().getEnvironment());
        AddressPool pool = new AddressPool(rsfEnv);
        String serviceID = "HelloWord_";
        //
        for (int i = 0; i < 10; i++) {
            pool.appendAddress(serviceID + i, new InterAddress("192.168.1.1", 8000, "etc2"));
        }
        //
        File inFile = new File(rsfEnv.getPluginDir(AddressPoolTest.class), "pool.zip");
        FileInputStream in = new FileInputStream(inFile);
        pool.restoreConfig(in);
        in.close();
        //
        Set<String> names = pool.getBucketNames();
        for (String service : names) {
            AddressBucket bucket = pool.getBucket(service);
            System.out.println(bucket.getServiceID() + " - address size = " + bucket.getAllAddresses().size());
        }
    }
    @Test
    public void localDiskCacheTest() throws IOException, URISyntaxException, InterruptedException {
        //
        // 1.修改默认配置
        DefaultRsfEnvironment rsfEnv = new DefaultRsfEnvironment(Hasor.createAppContext().getEnvironment());
        rsfEnv.getSettings().setSetting("hasor.rsfConfig.addressPool.refreshCacheTime", "1000");
        rsfEnv.getSettings().setSetting("hasor.rsfConfig.addressPool.diskCacheTimeInterval", "3000");
        rsfEnv.getSettings().setSetting("hasor.rsfConfig.addressPool.invalidWaitTime", "500");
        rsfEnv.getSettings().refreshRsfConfig();
        String serviceID = "HelloWord_";
        //
        // 2.测试本地缓存保存
        DiskCacheAddressPool pool = new DiskCacheAddressPool(rsfEnv);
        for (int i = 0; i < 10; i++) {
            configService(pool, serviceID + i);
        }
        pool.storeConfig();//保存一次
        //
        // 3.测试本地地址缓存加载。
        pool = new DiskCacheAddressPool(rsfEnv);
        for (int i = 0; i < 10; i++) {
            pool.appendAddress(serviceID + i, new InterAddress("192.168.1.1", 8000, "etc2"));
        }
        pool.restoreConfig();
    }
    //
    @Test
    public void nextAddressTest() throws IOException, URISyntaxException, InterruptedException {
        DefaultRsfEnvironment rsfEnv = new DefaultRsfEnvironment(Hasor.createAppContext().getEnvironment());
        final AddressPool pool = new AddressPool(rsfEnv);
        final String serviceID = "[RSF]test.net.hasor.rsf.services.EchoService-1.0.0";
        //
        ArrayList<InterAddress> dynamicList = new ArrayList<InterAddress>();
        dynamicList.add(new InterAddress("127.0.0.1", 8000, "etc2"));
        pool.appendAddress(serviceID, dynamicList);
        //
        ArrayList<InterAddress> staticList = new ArrayList<InterAddress>();
        staticList.add(new InterAddress("192.168.137.10", 8000, "etc2"));
        staticList.add(new InterAddress("192.168.137.11", 8000, "etc2"));
        staticList.add(new InterAddress("127.0.4.4", 8000, "etc2"));
        pool.appendStaticAddress(serviceID, staticList);
        //
        String flowBody = IOUtils.readToString(ResourcesUtils.getResourceAsStream("/flow-control/full-performance-flow.xml"));
        pool.updateFlowControl(serviceID, flowBody);
        //
        //
        Thread thread = new Thread() {
            @Override
            public void run() {
                Random random = new Random(System.currentTimeMillis());
                while (true) {
                    InterAddress address = pool.nextAddress(serviceID, "sayHello", new Object[] { "hello" });
                    System.out.println(Long.toHexString(random.nextLong()).toUpperCase() + "\t" + address);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        //
        Thread.sleep(5000);
        flowBody = IOUtils.readToString(ResourcesUtils.getResourceAsStream("/flow-control/full-flow.xml"));
        pool.updateFlowControl(serviceID, flowBody);
        //
        Thread.sleep(5000);
        String scriptBody = IOUtils.readToString(ResourcesUtils.getResourceAsStream("/rule-script/service-level.groovy"));
        pool.updateServiceRoute(serviceID, scriptBody);
        //
        Thread.sleep(5000);
        thread.stop();
    }
}