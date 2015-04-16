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
package test.net.hasor.rsf.address;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.BindCenter;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.address.AddressPool;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
import org.junit.Test;
import org.more.util.ResourcesUtils;
import org.more.util.io.IOUtils;
/**
 * 
 * @version : 2015年4月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class AddressPoolTest {
    private List<URI> addressList() throws IOException, URISyntaxException {
        List<URI> addresses = new ArrayList<URI>();
        addresses.add(new InterAddress("192.168.137.10", 8000, "etc2").toURI());//  rsf://192.168.137.10:8000/etc2
        addresses.add(new InterAddress("192.168.137.11", 8000, "etc2").toURI());//  rsf://192.168.137.11:8000/etc2
        addresses.add(new InterAddress("192.168.1.3", 8000, "etc3").toURI());//     rsf://192.168.1.3:8000/etc3
        addresses.add(new InterAddress("192.168.1.4", 8000, "etc3").toURI());//     rsf://192.168.1.4:8000/etc3
        addresses.add(new InterAddress("192.168.1.5", 8000, "etc3").toURI());//     rsf://192.168.1.5:8000/etc3
        //
        addresses.add(new URI("rsf://192.168.0.101:8000/etc2"));
        addresses.add(new URI("rsf://192.168.0.102:8000/etc2"));
        addresses.add(new URI("rsf://192.168.0.103:8000/etc2"));
        return addresses;
    }
    /**功能测试*/
    @Test
    public void poolTest() throws URISyntaxException, IOException, InterruptedException {
        BindCenter bindCenter = new TestBindCenter(AddressPoolTest.class);
        DefaultRsfSettings rsfSettings = new DefaultRsfSettings(new StandardContextSettings());
        rsfSettings.refresh();
        final AddressPool pool = new AddressPool("etc3", bindCenter, rsfSettings);
        final RsfBindInfo<?> domain = bindCenter.getServiceByName("AddressPoolTest");
        //
        String flowControlBody = IOUtils.toString(ResourcesUtils.getResourceAsStream("full-flow.xml"));
        pool.refreshFlowControl(flowControlBody);
        //
        System.err.println("\n\n start 3 Threads for doNextAddress.");
        //
        new Thread() { public void run() { doNextAddress("T1", domain, pool ,1); }; }.start();
        new Thread() { public void run() { doNextAddress("T2", domain, pool ,1); }; }.start();
        new Thread() { public void run() { doNextAddress("T3", domain, pool ,1); }; }.start();
        //
        //
        System.out.println("QoS = 5/s , Threads = 3.");
        Thread.sleep(3000);
        
        System.err.println("1s after start..");
        Thread.sleep(1000);
        System.err.println("newAddress.. unit = enable , only 'etc3'..");
        pool.newAddress(domain, addressList());
        startTime = System.currentTimeMillis() / 1000;//在这里设置启动时间用来测试 QoS 还是比较公平的.
        //
        Thread.sleep(5000);
        System.err.println("invalidAddress.. unit = disable, you can see 'etc2'..");
        pool.invalidAddress(domain, new URI("rsf://192.168.1.3:8000/etc3"));
        //
        Thread.sleep(5000);
        System.err.println("invalidAddress.. unit = enable, you can see 'etc3'..");
        pool.invalidAddress(domain, new URI("rsf://192.168.137.11:8000/etc2"));
        pool.invalidAddress(domain, new URI("rsf://192.168.137.10:8000/etc2"));
        //
        Thread.sleep(5000);
        System.err.println("invalidAddress.. unit = enable, only '192.168.1.5'");
        pool.invalidAddress(domain, new URI("rsf://192.168.0.101:8000/etc2"));
        pool.invalidAddress(domain, new URI("rsf://192.168.0.102:8000/etc2"));
        pool.invalidAddress(domain, new URI("rsf://192.168.1.4:8000/etc3"));
        //
        Thread.sleep(5000);
        System.err.println("invalidAddress.. unit = enable, only '192.168.0.103'");
        pool.invalidAddress(domain, new URI("rsf://192.168.1.5:8000/etc3"));
        //
        Thread.sleep(5000);
    }
    
    long startTime =0;
    java.util.concurrent.atomic.AtomicLong atomicLong = new AtomicLong(0);
    public void doNextAddress(final String tName, RsfBindInfo<?> domain, AddressPool pool,int showMod) {
        while (true) {
            InterAddress address = pool.nextAddress(domain, "methodssss", new Object[0]);
            if (address != null) {
                long i=atomicLong.getAndIncrement();
                if (i % showMod ==0){
                    long checkTime = System.currentTimeMillis() / 1000;
                    long speed = 0;
                    if (checkTime - startTime ==0){
                        speed = 0;
                    }else{
                        speed = (i / (checkTime - startTime));
                    }
                    System.out.println("["+tName+"]\t" + i + "\tSpeed(s):" + speed + "\t" + address);
                }
            }
        }
    }
    
    
    /**性能测试*/
    @Test
    public void performanceTest() throws URISyntaxException, IOException, InterruptedException {
        BindCenter bindCenter = new TestBindCenter(AddressPoolTest.class);
        DefaultRsfSettings rsfSettings = new DefaultRsfSettings(new StandardContextSettings());
        rsfSettings.refresh();
        final AddressPool pool = new AddressPool("etc3", bindCenter, rsfSettings);
        final RsfBindInfo<?> domain = bindCenter.getServiceByName("AddressPoolTest");
        //
        String flowControlBody = IOUtils.toString(ResourcesUtils.getResourceAsStream("full-performance-flow.xml"));
        pool.refreshFlowControl(flowControlBody);
        //
        //
        //
        int threadCount = 400;
        System.out.println("\n\n QoS = false , Threads = " + threadCount + ".");
        Thread.sleep(5000);
        
        System.err.println("1s after start..");
        Thread.sleep(1000);
        //
        for (int i =0;i<threadCount;i++){
            final int index = i;
            new Thread() { public void run() { doNextAddress("T-" + index, domain, pool , 10000); }; }.start();
            System.err.println("["+ i +"]Thread start..");
        }
        System.err.println("all Thread start..");
        //
        Thread.sleep(1000);
        System.err.println("newAddress..");
        pool.newAddress(domain, addressList());
        startTime = System.currentTimeMillis() / 1000;//在这里设置启动时间用来测试 QoS 还是比较公平的.
        //
        Thread.sleep(20000);
        
    }
}