/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.search.server.rsf.service;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfOptionSet;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.search.domain.OptionConstant;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2015年1月16日
 * @author 赵永春(zyc@hasor.net)
 */
public class ReadOptionFilter implements RsfFilter, OptionConstant {
    private final ThreadLocal<RsfOptionSet> optionSet = new ThreadLocal<RsfOptionSet>();
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        String coreName = request.getOption(CORE_NAME_KEY);
        if (StringUtils.isBlank(coreName) == true) {
            response.sendStatus(ProtocolStatus.Forbidden, "coreName is empty.");
            return;
        }
        //
        try {
            optionSet.set(request);
            chain.doFilter(request, response);
        } finally {
            optionSet.remove();
        }
    }
    /**获取操作的CoreName*/
    public RsfOptionSet getRsfOptionSet() {
        return this.optionSet.get();
    }
}