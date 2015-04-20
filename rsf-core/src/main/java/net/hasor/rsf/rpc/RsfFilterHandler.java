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
package net.hasor.rsf.rpc;
import java.util.Collections;
import java.util.List;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
/**
 * 负责处理 RsfFilter 调用
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfFilterHandler implements RsfFilterChain {
    private final List<RsfFilter> rsfFilter;
    private final RsfFilterChain  rsfChain;
    private int                   index;
    //
    public RsfFilterHandler(final List<RsfFilter> rsfFilter, final RsfFilterChain rsfChain) {
        this.rsfFilter = (rsfFilter == null) ? Collections.EMPTY_LIST : rsfFilter;
        this.rsfChain = rsfChain;
        this.index = -1;
    }
    public void doFilter(RsfRequest request, RsfResponse response) throws Throwable {
        this.index++;
        if (this.index < this.rsfFilter.size()) {
            this.rsfFilter.get(index).doFilter(request, response, this);
        } else {
            this.rsfChain.doFilter(request, response);
        }
    }
}