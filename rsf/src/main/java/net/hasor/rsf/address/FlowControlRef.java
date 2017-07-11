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
package net.hasor.rsf.address;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.address.route.flowcontrol.random.RandomFlowControl;
import net.hasor.rsf.address.route.flowcontrol.speed.SpeedFlowControl;
import net.hasor.rsf.address.route.flowcontrol.unit.UnitFlowControl;
import net.hasor.rsf.address.route.rule.Rule;
import net.hasor.rsf.address.route.rule.RuleParser;
import net.hasor.rsf.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
/**
 * 方便引用切换。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class FlowControlRef {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private static RuleParser ruleParser;
    public String            flowControlScript = null;
    public UnitFlowControl   unitFlowControl   = null; //单元规则
    public RandomFlowControl randomFlowControl = null; //地址选取规则
    public SpeedFlowControl  speedFlowControl  = null; //QoS速率规则
    //
    private FlowControlRef(RsfEnvironment rsfEnvironment) {
        if (ruleParser == null) {
            ruleParser = new RuleParser(rsfEnvironment);
        }
    }
    /**解析路由规则*/
    public void updateFlowControl(String flowControl) {
        if (StringUtils.isBlank(flowControl)) {
            logger.error("flowControl body is null.");
            return;
        } else {
            flowControl = flowControl.trim();
            if (!flowControl.startsWith("<controlSet") || !flowControl.endsWith("</controlSet>")) {
                logger.error("flowControl body format error.");
                return;
            }
        }
        this.flowControlScript = flowControl;
        //
        //1.提取路由配置
        List<String> ruleBodyList = new ArrayList<String>();
        final String tagNameBegin = "<flowControl";
        final String tagNameEnd = "</flowControl>";
        int beginIndex = 0;
        int endIndex = 0;
        while (true) {
            beginIndex = flowControl.indexOf(tagNameBegin, endIndex);
            endIndex = flowControl.indexOf(tagNameEnd, endIndex + tagNameEnd.length());
            if (beginIndex < 0 || endIndex < 0) {
                break;
            }
            String flowControlBody = flowControl.substring(beginIndex, endIndex + tagNameEnd.length());
            ruleBodyList.add(flowControlBody);
        }
        if (ruleBodyList.isEmpty()) {
            logger.warn("flowControl is empty.");
            return;
        }
        //2.解析路由配置
        for (int i = 0; i < ruleBodyList.size(); i++) {
            String controlBody = ruleBodyList.get(i);
            Rule rule = this.ruleParser.ruleSettings(controlBody);
            if (rule == null) {
                continue;
            }
            String simpleName = rule.getClass().getSimpleName();
            logger.info("setup flowControl type is {}.", simpleName);
            /*  */
            if (rule instanceof UnitFlowControl) {
                this.unitFlowControl = (UnitFlowControl) rule; /*单元规则*/
            } else if (rule instanceof RandomFlowControl) {
                this.randomFlowControl = (RandomFlowControl) rule;/*选址规则*/
            } else if (rule instanceof SpeedFlowControl) {
                this.speedFlowControl = (SpeedFlowControl) rule; /*速率规则*/
            }
        }
        return;
    }
    //
    //
    public static final FlowControlRef newRef(RsfEnvironment rsfEnvironment, FlowControlRef ref) {
        FlowControlRef newRef = defaultRef(rsfEnvironment);
        if (!StringUtils.isBlank(ref.flowControlScript)) {
            newRef.flowControlScript = ref.flowControlScript;
        }
        if (ref.unitFlowControl != null) {
            newRef.unitFlowControl = ref.unitFlowControl;
        }
        if (ref.randomFlowControl != null) {
            newRef.randomFlowControl = ref.randomFlowControl;
        }
        if (ref.speedFlowControl != null) {
            newRef.speedFlowControl = ref.speedFlowControl;
        }
        return newRef;
    }
    public static final FlowControlRef defaultRef(RsfEnvironment rsfEnvironment) {
        FlowControlRef flowControlRef = new FlowControlRef(rsfEnvironment);
        flowControlRef.randomFlowControl = new RandomFlowControl();
        flowControlRef.speedFlowControl = SpeedFlowControl.defaultControl(rsfEnvironment);
        return flowControlRef;
    }
}