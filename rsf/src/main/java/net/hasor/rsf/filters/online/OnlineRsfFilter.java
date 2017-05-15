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
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.domain.ProtocolStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 一旦下线，所有远程的连入请求都被回绝：Forbidden
 * @version : 2016年3月23日
 * @author 赵永春(zyc@hasor.net)
 */
public class OnlineRsfFilter implements RsfFilter {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        if (!request.isLocal() && !request.getContext().isOnline()) {
            response.sendStatus(ProtocolStatus.Forbidden, "the service is not yet ready.");
            return;
        }
        chain.doFilter(request, response);
    }
}