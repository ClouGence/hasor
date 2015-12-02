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
import org.junit.Test;
import org.more.util.ResourcesUtils;
import org.more.util.io.IOUtils;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.address.route.rule.Rule;
import net.hasor.rsf.address.route.rule.RuleParser;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
/**
 * 
 * @version : 2015年4月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class FlowControlReadTest {
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
        String randomBody = IOUtils.toString(ResourcesUtils.getResourceAsStream("/flowcontrol-script/random-flow.xml"));
        //
        Rule rule = ruleParser.ruleSettings(randomBody);
        System.out.println(rule);
    }
    @Test
    public void roomTest() throws Throwable {
        RuleParser ruleParser = getRuleParser();
        String unitBody = IOUtils.toString(ResourcesUtils.getResourceAsStream("/flowcontrol-script/unit-flow.xml"));
        //
        Rule rule = ruleParser.ruleSettings(unitBody);
        System.out.println(rule);
    }
    @Test
    public void speedTest() throws Throwable {
        RuleParser ruleParser = getRuleParser();
        String speedBody = IOUtils.toString(ResourcesUtils.getResourceAsStream("/flowcontrol-script/speed-flow.xml"));
        //
        Rule rule = ruleParser.ruleSettings(speedBody);
        System.out.println(rule);
    }
}