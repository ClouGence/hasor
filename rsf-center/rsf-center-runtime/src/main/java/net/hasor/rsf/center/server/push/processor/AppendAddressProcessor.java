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
package net.hasor.rsf.center.server.push.processor;
import net.hasor.core.Singleton;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.center.server.push.PushEvent;
import net.hasor.rsf.center.server.push.PushProcessor;
/**
 * 追加或重新激活地址。
 * @see net.hasor.rsf.center.server.push.RsfCenterPushEventEnum#AppendAddressEvent
 * @version : 2016年3月24日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class AppendAddressProcessor extends PushProcessor {
    @Override
    public void doProcessor(RsfClient rsfClient, PushEvent event) {
        rsfClient.syncInvoke(bindInfo, methodName, parameterTypes, parameterObjects);
        // TODO Auto-generated method stub
    }
}