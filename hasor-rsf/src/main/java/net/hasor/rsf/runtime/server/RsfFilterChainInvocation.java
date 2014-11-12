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
package net.hasor.rsf.runtime.server;
import net.hasor.rsf.runtime.RsfFilter;
import net.hasor.rsf.runtime.RsfFilterChain;
import net.hasor.rsf.runtime.RsfRequest;
import net.hasor.rsf.runtime.RsfResponse;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfFilterChainInvocation implements RsfFilterChain {
    private RsfFilter[]    rsfFilters = null;
    private RsfFilterChain finalChain = null;
    private int            index      = -1;
    //
    public RsfFilterChainInvocation(final RsfFilterChain finalChain) {
        this(new RsfFilter[0], finalChain);
    }
    public RsfFilterChainInvocation(final RsfFilter[] rsfFilters, final RsfFilterChain finalChain) {
        this.rsfFilters = (rsfFilters == null) ? new RsfFilter[0] : rsfFilters;
        this.finalChain = finalChain;
    }
    @Override
    public void doFilter(RsfRequest request, RsfResponse response) throws Throwable {
        this.index++;
        if (this.index < this.rsfFilters.length) {
            this.rsfFilters[this.index].doFilter(request, response, this);
        } else {
            this.finalChain.doFilter(request, response);
        }
    }
}