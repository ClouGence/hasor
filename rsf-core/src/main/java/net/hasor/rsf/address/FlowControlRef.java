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
package net.hasor.rsf.address;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.route.flowcontrol.random.RandomFlowControl;
import net.hasor.rsf.address.route.flowcontrol.speed.SpeedFlowControl;
import net.hasor.rsf.address.route.flowcontrol.unit.UnitFlowControl;
/**
 * 方便引用切换。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
class FlowControlRef {
    public UnitFlowControl   unitFlowControl   = null; //单元规则
    public RandomFlowControl randomFlowControl = null; //地址选取规则
    public SpeedFlowControl  speedFlowControl  = null; //QoS速率规则
    //
    private FlowControlRef() {}
    //
    public static final FlowControlRef defaultRef(RsfSettings rsfSettings) {
        FlowControlRef flowControlRef = new FlowControlRef();
        flowControlRef.randomFlowControl = new RandomFlowControl();
        flowControlRef.speedFlowControl = SpeedFlowControl.defaultControl(rsfSettings);
        return flowControlRef;
    }
}