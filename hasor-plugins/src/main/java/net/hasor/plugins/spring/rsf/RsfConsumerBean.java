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
package net.hasor.plugins.spring.rsf;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfPublisher;

import java.util.List;
/**
 * 服务消费者
 * @version : 2016-11-08
 * @author 赵永春 (zyc@hasor.net)
 */
public class RsfConsumerBean extends AbstractRsfBean {
    private InterAddress       target;
    private List<InterAddress> targetList;
    private boolean            onMessage;
    private Object             warpBean;
    //
    public boolean isOnMessage() {
        return onMessage;
    }
    public void setOnMessage(boolean onMessage) {
        this.onMessage = onMessage;
    }
    public InterAddress getTarget() {
        return target;
    }
    public void setTarget(InterAddress target) {
        this.target = target;
    }
    public List<InterAddress> getTargetList() {
        return targetList;
    }
    public void setTargetList(List<InterAddress> targetList) {
        this.targetList = targetList;
    }
    //
    @Override
    public Object getObject() throws Exception {
        if (this.warpBean == null) {
            this.warpBean = this.getRsfClient().wrapper(this.getBindType());
        }
        return this.warpBean;
    }
    @Override
    protected RsfPublisher.RegisterBuilder<?> configService(RsfPublisher.RegisterBuilder<?> builder) {
        if (this.isOnMessage()) {
            builder = builder.asMessage();
        }
        // .目标服务地址
        if (this.getTarget() != null) {
            builder.bindAddress(this.getTarget());
        }
        List<InterAddress> targetList = this.getTargetList();
        if (targetList != null && !targetList.isEmpty()) {
            builder.bindAddress(null, targetList.toArray(new InterAddress[targetList.size()]));
        }
        //
        return builder;
    }
}