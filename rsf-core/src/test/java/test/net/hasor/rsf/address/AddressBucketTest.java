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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import net.hasor.rsf.address.AddressBucket;
import net.hasor.rsf.address.InterAddress;
import org.junit.Test;
/**
 * 
 * @version : 2015年4月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class AddressBucketTest {
    @Test
    public void bucketTest() throws Throwable {
        final AddressBucket bucket = new AddressBucket("myService", "wtc1");
        //
        new Thread() { public void run() { this.setName("T1"); printAddress(bucket); }; }.start();
        new Thread() { public void run() { this.setName("T2"); printAddress(bucket); }; }.start();
        new Thread() { public void run() { this.setName("T3"); printAddress(bucket); }; }.start();
        new Thread() { public void run() { this.setName("T4"); printAddress(bucket); }; }.start();
        //
        Thread.sleep(3000);
        List<URI> addressList1 = new ArrayList<URI>();
        addressList1.add(new URI("rsf://192.168.0.1:8000/etc2"));//机房2
        addressList1.add(new URI("rsf://192.168.0.2:8000/etc2"));//机房2
        addressList1.add(new URI("rsf://192.168.0.3:8000/wtc1"));//机房1
        bucket.newAddress(addressList1);
        //
        //
        Thread.sleep(3000);
        List<URI> addressList2 = new ArrayList<URI>();
        addressList2.add(new URI("rsf://192.168.0.4:8000/etc2"));//机房2
        addressList2.add(new URI("rsf://192.168.0.5:8000/etc2"));//机房2
        addressList2.add(new URI("rsf://192.168.0.6:8000/wtc1"));//机房1
        bucket.newAddress(addressList2);
    }
    //
    //
    //
    public void printAddress(AddressBucket bucket) {
        int i=1000;
        while (true) {
            try { Thread.sleep(10); } catch (InterruptedException e) { }
            List<InterAddress> localList = bucket.getLocalUnitAddresses();
            List<InterAddress> availList = bucket.getAvailableAddresses();
            i++;
            if (i<1000){
                continue;
            }
            //
            i=0;
            for (InterAddress local : localList) {
                System.out.println("["+Thread.currentThread().getName()+"]Local:" + local);
            }
            for (InterAddress avail : availList) {
                System.out.println("["+Thread.currentThread().getName()+"]Avail:" + avail);
            }
            System.out.println("["+Thread.currentThread().getName()+"]print finish!\n");
        }
    }
}