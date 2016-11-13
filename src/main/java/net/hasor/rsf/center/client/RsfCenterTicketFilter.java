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
package net.hasor.rsf.center.client;
import net.hasor.core.Singleton;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.center.RsfCenterRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 负责更新服务的 Center的服务注册ID
 * @version : 2016年11月13日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class RsfCenterTicketFilter implements RsfFilter {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        // .执行处理
        chain.doFilter(request, response);
        // .处理返回值
        boolean needProcess = request.getMethod().getDeclaringClass().isAssignableFrom(RsfCenterRegister.class);
        if (!needProcess || response.getData() == null) {
            return;
        }
        //
        Object data = response.getData();
        //
        logger.info("update CenterSnapshotInfo success -> serviceID={}.", request.getBindInfo().getBindID());
        //        String ticketInfo = eventData.getTicketID();
        //        if (StringUtils.isNotBlank(ticketInfo)) {
        //            domain.setMetaData(RsfConstants.Center_Ticket, ticketInfo);
        //        }
    }
}