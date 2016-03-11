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
package net.hasor.rsf.center.core.diplomat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.EventListener;
import net.hasor.core.Inject;
import net.hasor.plugins.event.Event;
import net.hasor.rsf.center.domain.constant.RsfCenterCfg;
import net.hasor.rsf.center.domain.constant.RsfCenterEvent;
/**
 * 负责处理当Leader变更通知。
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
@Event(RsfCenterEvent.ConfirmLeader_Event)
public class LeaderDiplomat implements EventListener<DataDiplomat> {
    protected Logger     logger = LoggerFactory.getLogger(getClass());
    @Inject
    private RsfCenterCfg rsfCenterCfg;
    //
    @Override
    public void onEvent(String event, DataDiplomat eventData) throws Throwable {
        // TODO Auto-generated method stub
    }
}