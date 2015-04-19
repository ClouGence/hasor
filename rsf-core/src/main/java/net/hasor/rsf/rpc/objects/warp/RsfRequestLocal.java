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
import net.hasor.rsf.RsfRequest;
/**
 * {@link RsfRequest}接口包装器（当前线程绑定）。
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfRequestLocal extends AbstractRsfRequestWarp {
    private static final ThreadLocal<RsfRequest> LOCAL_REQUEST = new ThreadLocal<RsfRequest>();
    @Override
    protected final RsfRequest getRsfRequest() {
        return LOCAL_REQUEST.get();
    }
    //
    static void removeLocal() {
        if (LOCAL_REQUEST.get() != null) {
            LOCAL_REQUEST.remove();
        }
    }
    static void updateLocal(RsfRequest rsfRequest) {
        removeLocal();
        if (rsfRequest != null) {
            LOCAL_REQUEST.set(rsfRequest);
        }
    }
}