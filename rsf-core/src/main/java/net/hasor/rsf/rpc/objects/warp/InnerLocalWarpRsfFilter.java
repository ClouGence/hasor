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
package net.hasor.rsf.rpc.objects.warp;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
/**
 * 负责更新{@link RsfRequestLocal}、{@link RsfResponseLocal}
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public final class InnerLocalWarpRsfFilter implements RsfFilter {
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        try {
            RsfRequestLocal.updateLocal(request);
            RsfResponseLocal.updateLocal(response);
            chain.doFilter(request, response);
        } finally {
            RsfRequestLocal.removeLocal();
            RsfResponseLocal.removeLocal();
        }
    }
}