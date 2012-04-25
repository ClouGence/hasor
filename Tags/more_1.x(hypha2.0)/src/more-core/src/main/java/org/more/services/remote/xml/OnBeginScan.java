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
package org.more.services.remote.xml;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.Event.Sequence;
import org.more.hypha.EventListener;
import org.more.hypha.anno.BeginScanEvent;
import org.more.hypha.anno.BeginScanEvent.BeginScanEvent_Params;
import org.more.services.remote.Remote;
import org.more.services.remote.RemoteService;
/**
 * 该类负责注册注解扫描器。
 * @version : 2011-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
class OnBeginScan implements EventListener<BeginScanEvent> {
    private static Log    log     = LogFactory.getLog(OnBeginScan.class);
    private RemoteService service = null;
    /**该类负责注册注解扫描器。*/
    public OnBeginScan(RemoteService service) {
        this.service = service;
    };
    public void onEvent(BeginScanEvent event, Sequence sequence) throws Throwable {
        BeginScanEvent_Params params = event.toParams(sequence);
        log.debug("regedit Remote Anno Watch to remoteService.");
        params.annoService.registerAnnoKeepWatch(Remote.class, new Watch_Remote(this.service));
    };
};