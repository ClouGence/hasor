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
import net.hasor.rsf.InterAddress;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
 *
 * @version : 2015年4月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class AddressUnitTest {
    @Test
    public void protocol() throws URISyntaxException {
        ConcurrentMap<InterAddress, String> concurrentMap = new ConcurrentHashMap<InterAddress, String>();
        //
        concurrentMap.put(new InterAddress("127.0.0.1", 8000, "etc2"), "123");
        concurrentMap.put(new InterAddress("127.0.0.1", 8000, "etc2"), "123");
        //
        assert concurrentMap.size() == 1;
        System.out.println(concurrentMap.size());
    }
    @Test
    public void test() throws URISyntaxException {
        ConcurrentMap<InterAddress, String> concurrentMap = new ConcurrentHashMap<InterAddress, String>();
        //
        concurrentMap.put(new InterAddress("127.0.0.1", 8000, "etc2"), "123");
        concurrentMap.put(new InterAddress("127.0.0.1", 8000, "etc2"), "123");
        //
        assert concurrentMap.size() == 1;
        System.out.println(concurrentMap.size());
    }
    @Test
    public void unitAddress() throws URISyntaxException {
        InterAddress unit = new InterAddress("rsf://127.0.0.1:8000/unit");
        System.out.println(unit);
    }
    @Test
    public void ipAddress() throws URISyntaxException {
        InterAddress interAddress1 = new InterAddress("127.0.0.1", 8000, "etc2");
        InterAddress interAddress2 = new InterAddress("127.0.0.1", 8000, "etc2");
        InterAddress interAddress3 = new InterAddress("rsf://127.0.0.1:8000/etc2");
        InterAddress interAddress4 = new InterAddress("RSF://127.0.0.1:8000/etc2");
        //
        boolean eq1 = interAddress1.equals(interAddress2);
        boolean eq2 = interAddress1.equals(interAddress3);
        boolean eq3 = interAddress1.equals(interAddress4);
        //
        assert eq1 && eq2 && eq3;
        //
        System.out.println(eq1 + "\t" + eq2 + "\t" + eq3);
        //
        System.out.println(interAddress1);
        System.out.println(interAddress3);
        System.out.println(interAddress4);
    }
}