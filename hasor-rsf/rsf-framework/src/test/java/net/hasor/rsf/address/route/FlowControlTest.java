package net.hasor.rsf.address.route;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.address.route.flowcontrol.random.RandomFlowControl;
import net.hasor.rsf.address.route.flowcontrol.speed.SpeedFlowControl;
import net.hasor.rsf.address.route.flowcontrol.unit.UnitFlowControl;
import net.hasor.rsf.address.route.rule.RuleParser;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.utils.IOUtils;
import net.hasor.utils.ResourcesUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlowControlTest {
    private List<InterAddress> addressList() {
        List<InterAddress> addresses = new ArrayList<>();
        addresses.add(new InterAddress("192.168.137.1", 8000, "etc2"));
        addresses.add(new InterAddress("192.168.137.2", 8000, "etc2"));
        addresses.add(new InterAddress("192.168.1.3", 8000, "etc3"));
        addresses.add(new InterAddress("192.168.1.4", 8000, "etc3"));
        return addresses;
    }

    private RuleParser getRuleParser() throws IOException {
        RsfEnvironment settings = new DefaultRsfEnvironment(new StandardEnvironment());
        return new RuleParser(settings);
    }

    @Test
    public void randomTest() throws Throwable {
        RuleParser ruleParser = getRuleParser();
        String randomBody = IOUtils.readToString(ResourcesUtils.getResourceAsStream(//
                "/net_hasor_rsf_route/flowcontrol-random.xml"), "utf-8");
        RandomFlowControl rule = (RandomFlowControl) ruleParser.ruleSettings(randomBody);
        List<InterAddress> addressPool = addressList();
        //
        Set<InterAddress> resultSet = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            InterAddress addr = rule.getServiceAddress(addressPool);
            System.out.println(i + "\t" + addr);
        }
    }

    @Test
    public void unitTest() throws Throwable {
        RuleParser ruleParser = getRuleParser();
        String roomBody = IOUtils.readToString(ResourcesUtils.getResourceAsStream("/flow-control/unit-flow.xml"), "utf-8");
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
        String speedBody = IOUtils.readToString(ResourcesUtils.getResourceAsStream("/flow-control/speed-flow.xml"), "utf-8");
        //
        SpeedFlowControl rule = (SpeedFlowControl) ruleParser.ruleSettings(speedBody);
        InterAddress doCallAddress = addressList().get(0);
        //
        int run = 0;
        long startTime = System.currentTimeMillis() / 1000;
        Thread.sleep(1000);
        for (int i = 0; i < 300000; i++) {
            if (rule.callCheck("serviceID", "methodName", doCallAddress)) {
                run++;
                long checkTime = System.currentTimeMillis() / 1000;
                if (run % 20 == 0) {
                    System.out.println(i + "\tCount:" + run + "\tSpeed(s):" + (run / (checkTime - startTime)));
                }
            }
        }
    }
}
