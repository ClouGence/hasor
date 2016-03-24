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
package net.hasor.rsf.filters.online;
import java.util.concurrent.atomic.AtomicBoolean;
import org.more.util.StringUtils;
import net.hasor.core.EventContext;
import net.hasor.core.EventListener;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfEvent;
/**
 * 一旦下线，所有远程的连入请求都被回绝：Forbidden
 * @version : 2016年3月23日
 * @author 赵永春(zyc@hasor.net)
 */
public class OnlineRsfFilter implements RsfFilter, EventListener<Object> {
    private final AtomicBoolean inited = new AtomicBoolean(false);
    public OnlineRsfFilter(RsfContext rsfContext) {
        EventContext ec = rsfContext.getAppContext().getEnvironment().getEventContext();
        ec.addListener(RsfEvent.Rsf_Online, this);
        ec.addListener(RsfEvent.Rsf_Offline, this);
    }
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        if (request.isLocal() == false && this.inited.get() == false) {
            response.sendStatus(ProtocolStatus.Forbidden, "the service is not yet ready.");
            return;
        }
        chain.doFilter(request, response);
    }
    @Override
    public void onEvent(String event, Object eventData) throws Throwable {
        if (StringUtils.equalsIgnoreCase(event, RsfEvent.Rsf_Online)) {
            this.inited.set(true);
        }
        if (StringUtils.equalsIgnoreCase(event, RsfEvent.Rsf_Offline)) {
            this.inited.set(false);
        }
    }
}