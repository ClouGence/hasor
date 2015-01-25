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
import net.hasor.search.client.Commit;
import net.hasor.search.domain.OptionConstant;
/**
 * 不要把它加入全局过滤器，以免被误杀抛出 403.
 * @version : 2015年1月8日
 * @author 赵永春(zyc@hasor.net)
 */
class CommitOptionFilter implements RsfFilter, OptionConstant {
    private Commit commitMode = null;
    public CommitOptionFilter(Commit commitMode) {
        this.commitMode = commitMode;
    }
    //
    @Override
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        if (this.commitMode != null) {
            request.addOption(COMMIT_KEY, COMMIT_VALUE);
            request.addOption(WAIT_FLUSH_KEY, Boolean.toString(this.commitMode.waitFlush()));
            request.addOption(WAIT_SEARCHER_KEY, Boolean.toString(this.commitMode.waitSearcher()));
            request.addOption(SOFT_COMMIT_KEY, Boolean.toString(this.commitMode.softCommit()));
        }
        chain.doFilter(request, response);
    }
}