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
package net.hasor.search.client.rsf;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.constants.ProtocolStatus;
import org.more.util.StringUtils;
/**
 * 不要把它加入全局过滤器，以免被误杀抛出 403.
 * @version : 2015年1月8日
 * @author 赵永春(zyc@hasor.net)
 */
class CoreNameFilter implements RsfFilter {
    private final ThreadLocal<String> useCoreName = new ThreadLocal<String>();
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        String coreName = this.useCoreName.get();
        if (StringUtils.isBlank(coreName) == true) {
            response.sendStatus(ProtocolStatus.Forbidden, "coreName is empty.");
            return;
        }
        request.addOption("CoreName", coreName);
        chain.doFilter(request, response);
    }
    public void useCoreName(String coreName) {
        if (useCoreName.get() != null)
            useCoreName.remove();
        useCoreName.set(coreName);
    }
}