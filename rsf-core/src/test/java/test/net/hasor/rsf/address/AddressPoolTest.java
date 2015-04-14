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
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.address.AddressPool;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.domain.ServiceDomain;
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
        addresses.add(new InterAddress("192.168.137.1", 8000, "etc2").toURI());
        addresses.add(new InterAddress("192.168.137.2", 8000, "etc2").toURI());
        addresses.add(new InterAddress("192.168.1.3", 8000, "etc3").toURI());
        addresses.add(new InterAddress("192.168.1.4", 8000, "etc3").toURI());
        //
        addresses.add(new URI("rsf://192.168.0.1:8000/etc2"));
        addresses.add(new URI("rsf://192.168.0.2:8000/etc2"));
        addresses.add(new URI("rsf://192.168.0.3:8000/etc2"));
        return addresses;
    }
    @Test
    public void poolTest() throws URISyntaxException, IOException, InterruptedException {
        DefaultRsfSettings rsfSettings = new DefaultRsfSettings(new StandardContextSettings());
        rsfSettings.refresh();
        final AddressPool pool = new AddressPool("etc2", rsfSettings);
        final ServiceDomain<?> domain = new ServiceDomain<AddressPoolTest>(AddressPoolTest.class);
        //
        String flowControlBody = IOUtils.toString(ResourcesUtils.getResourceAsStream("full-flow.xml"));
        pool.refreshFlowControl(flowControlBody);
        //
        new Thread() { public void run() { doNextAddress(domain, pool); }; }.start();
        //
        Thread.sleep(1000);
        pool.newAddress(domain, addressList());
        //
        Thread.sleep(10000);
    }
    public void doNextAddress(ServiceDomain<?> domain, AddressPool pool) {
        int i = 0;
        while (true) {
            InterAddress address = pool.nextAddress(domain, "methodssss", new Object[0]);
            System.out.println(i++ + "\t" + address);
        }
    }
}