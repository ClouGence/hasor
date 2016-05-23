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
package net.hasor.rsf.center.server.data.datafilter;
import java.util.Date;
import org.more.datachain.DataFilter;
import org.more.datachain.DataFilterChain;
import org.more.datachain.Domain;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.center.server.domain.entity.ServerDO;
import net.hasor.rsf.center.server.domain.entity.StatusEnum;
/**
 * 
 * @version : 2016年5月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfContext2ServerDODataFilter implements DataFilter<RsfContext, ServerDO> {
    @Override
    public ServerDO doForward(Domain<RsfContext> domain, DataFilterChain<RsfContext, ServerDO> chain) throws Throwable {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public RsfContext doBackward(Domain<ServerDO> domain, DataFilterChain<RsfContext, ServerDO> chain) throws Throwable {
        // TODO Auto-generated method stub
        return null;
    }
}
/*
 ServerDO serverDO = new ServerDO();
            serverDO.setRsfUrl(rsfHost);
            serverDO.setBindAddress(rsfHost.getHost());
            serverDO.setBindPort(rsfHost.getPort());
            serverDO.setUnit(rsfHost.getFormUnit());
            serverDO.setVersion(rsfContext.getSettings().getVersion());
            serverDO.setBeat(new Date());
            serverDO.setSaltValue(System.currentTimeMillis());
            serverDO.setStatus(StatusEnum.online);
*/