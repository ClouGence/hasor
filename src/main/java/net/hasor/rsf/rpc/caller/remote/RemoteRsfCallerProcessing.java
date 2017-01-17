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
package net.hasor.rsf.rpc.caller.remote;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 负责处理远程Request对象的请求调用，同时也负责将产生的Response对象写回客户端。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class RemoteRsfCallerProcessing extends InvokerProcessing {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    public RemoteRsfCallerProcessing(InterAddress target, RemoteRsfCaller rsfCaller, RequestInfo requestInfo) {
        super(target, rsfCaller, requestInfo);
    }
    @Override
    protected void doSendResponse(ResponseInfo info) {
        this.getRsfCaller().getSenderListener().sendResponse(this.getTarget(), info);
    }
}