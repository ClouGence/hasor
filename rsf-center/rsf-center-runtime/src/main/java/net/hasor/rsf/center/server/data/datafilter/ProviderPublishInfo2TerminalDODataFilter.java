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
import net.hasor.rsf.center.domain.ProviderPublishInfo;
import net.hasor.rsf.center.server.domain.entity.TerminalDO;
import org.more.bizcommon.datachain.DataFilter;
import org.more.bizcommon.datachain.DataFilterChain;
import org.more.bizcommon.datachain.Domain;
/**
 *
 * @version : 2016年5月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProviderPublishInfo2TerminalDODataFilter implements DataFilter<ProviderPublishInfo, TerminalDO> {
    @Override
    public TerminalDO doForward(Domain<ProviderPublishInfo> domain, DataFilterChain<ProviderPublishInfo, TerminalDO> chain) throws Throwable {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public ProviderPublishInfo doBackward(Domain<TerminalDO> domain, DataFilterChain<ProviderPublishInfo, TerminalDO> chain) throws Throwable {
        throw new java.lang.UnsupportedOperationException();
    }
}