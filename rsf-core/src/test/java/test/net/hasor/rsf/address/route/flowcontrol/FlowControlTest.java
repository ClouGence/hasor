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
package test.net.hasor.rsf.address.route.flowcontrol;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.address.route.flowcontrol.random.RandomFlowControl;
import net.hasor.rsf.address.route.flowcontrol.speed.SpeedFlowControl;
import net.hasor.rsf.address.route.flowcontrol.unit.UnitFlowControl;
import net.hasor.rsf.address.route.rule.RuleParser;
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
public class FlowControlTest {
    private List<InterAddress> addressList() throws IOException, URISyntaxException {
        List<InterAddress> addresses = new ArrayList<InterAddress>();
        addresses.add(new InterAddress("192.168.137.1", 8000, "etc2"));
        addresses.add(new InterAddress("192.168.137.2", 8000, "etc2"));
        addresses.add(new InterAddress("192.168.1.3", 8000, "etc3"));
        addresses.add(new InterAddress("192.168.1.4", 8000, "etc3"));
        return addresses;
    }
    private RuleParser getRuleParser() throws IOException, URISyntaxException {
        DefaultRsfSettings rsfSettings = new DefaultRsfSettings(new StandardContextSettings());
        rsfSettings.refresh();
        RuleParser parser = new RuleParser(rsfSettings);
        return parser;
    }
    //
    @Test
    public void randomTest() throws Throwable {
        RuleParser ruleParser = getRuleParser();
        String randomBody = IOUtils.toString(ResourcesUtils.getResourceAsStream("random-flow.xml"));
        //
        RandomFlowControl rule = (RandomFlowControl) ruleParser.ruleSettings(randomBody);
        List<InterAddress> address = addressList();
        //
        for (int i = 0; i < 100; i++) {
            InterAddress addr = rule.getServiceAddress(address);
            System.out.println(i + "\t" + addr);
        }
    }
    //    @Test
    //    public void networkTest() throws Throwable {
    //        RuleParser ruleParser = getRuleParser();
    //        String roomBody = IOUtils.toString(ResourcesUtils.getResourceAsStream("network-flow.xml"));
    //        //
    //        NetworkFlowControl rule = (NetworkFlowControl) ruleParser.ruleSettings(roomBody);
    //        List<InterAddress> address = addressList();
    //        //
    //        List<InterAddress> addrList = rule.siftNetworkAddress(address);
    //        System.out.println(addrList);
    //    }
    @Test
    public void unitTest() throws Throwable {
        RuleParser ruleParser = getRuleParser();
        String roomBody = IOUtils.toString(ResourcesUtils.getResourceAsStream("unit-flow.xml"));
        //
        UnitFlowControl rule = (UnitFlowControl) ruleParser.ruleSettings(roomBody);
        List<InterAddress> address = addressList();
        //
        List<InterAddress> addrList = rule.siftUnitAddress("etc2", address);
        System.out.println(addrList);
    }
    @Test
    public void speedTest() throws Throwable {
        RuleParser ruleParser = getRuleParser();
        String speedBody = IOUtils.toString(ResourcesUtils.getResourceAsStream("speed-flow.xml"));
        //
        SpeedFlowControl rule = (SpeedFlowControl) ruleParser.ruleSettings(speedBody);
        InterAddress doCallAddress = addressList().get(0);
        String m = FlowControlTest.class.getMethods()[0].toString();
        RsfBindInfo<?> info = new ServiceDomain<FlowControlTest>(FlowControlTest.class);
        //
        int run = 0;
        long startTime = System.currentTimeMillis() / 1000;
        Thread.sleep(1000);
        for (int i = 0; i < 300000; i++) {
            if (rule.callCheck(info, m, doCallAddress) == true) {
                run++;
                long checkTime = System.currentTimeMillis() / 1000;
                if (run % 20 == 0) {
                    System.out.println(i + "\tCount:" + run + "\tSpeed(s):" + (run / (checkTime - startTime)));
                }
            }
        }
    }
}