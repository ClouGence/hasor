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
package net.hasor.rsf.center.server.data;
import java.util.Date;

import net.hasor.rsf.center.server.domain.query.TerminalQuery;
import org.more.bizcommon.PageResult;
import org.more.bizcommon.Result;
import net.hasor.rsf.center.server.domain.entity.ServerDO;
import net.hasor.rsf.center.server.domain.entity.ServiceDO;
import net.hasor.rsf.center.server.domain.entity.TerminalDO;
/**
 *
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public interface DataAdapter {
    //
    //
    public Result<Long> insertService(ServiceDO serviceDO);

    public Result<ServiceDO> queryServiceByHashCode(String hashCode);

    public Result<Boolean> beatOfService(String forBindID, String hostPort, String saltValue, Date beatTime);

    public Result<Boolean> offlineService(String forBindID, String hostPort, String saltValue);

    public Result<Boolean> onlineService(TerminalDO terminalDO);

    //
    //
    public Result<ServerDO> queryServerByHost(String bindAddress, int bindPort);

    public Result<Long> insertServer(ServerDO serverDO);

    public Result<Boolean> updateServer(ServerDO serverDO);
 
    public Result<Boolean> beatOfServer(long serverID, Date beatTime);

    PageResult<String> queryTerminalByQuery(TerminalQuery query);
}