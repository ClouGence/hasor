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
import io.netty.channel.Channel;
import java.lang.reflect.Method;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.general.RsfException;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.net.netty.NetworkChanne;
import net.hasor.rsf.protocol.message.RequestMsg;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.RsfFilter;
import net.hasor.rsf.runtime.RsfFilterChain;
import net.hasor.rsf.runtime.common.RsfRequestImpl;
import net.hasor.rsf.runtime.common.RsfResponseImpl;
/**
 * 调用主逻辑
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerInvokeHandler implements Runnable {
    private RsfContext rsfContext = null;
    private RequestMsg requestMsg = null;
    private Channel    channel    = null;
    //
    public InnerInvokeHandler(RsfContext rsfContext, RequestMsg requestMsg, Channel channel) {
        this.rsfContext = rsfContext;
        this.requestMsg = requestMsg;
        this.channel = channel;
    }
    public void run() {
        NetworkChanne connection = new NetworkChanne(channel);
        RsfRequestImpl request = new RsfRequestImpl(requestMsg, connection, rsfContext);
        RsfResponseImpl response = new RsfResponseImpl(request, rsfContext);
        this.process(request, response);
    }
    private void process(RsfRequestImpl request, RsfResponseImpl response) {
        //1.初始化
        try {
            request.init();
            response.init();
        } catch (RsfException e) {
            response.sendStatus(e.getStatus(), e);
            return;
        }
        //
        //2.检查timeout
        long lostTime = System.currentTimeMillis() - request.getReceiveTime();
        if (lostTime > request.getTimeout()) {
            response.sendStatus(ProtocolStatus.RequestTimeout, "request timeout. (client parameter).");
            return;
        }
        //
        //3.check target Object
        ServiceMetaData metaData = request.getMetaData();
        Method targetMethod = request.getTargetMethod();
        Object targetObj = this.rsfContext.getBean(metaData);
        if (targetObj == null) {
            response.sendStatus(ProtocolStatus.Forbidden, "failed to get service.");
            return;
        }
        //
        //4.执行调用
        try {
            RsfFilterChain rsfChain = new InnerRsfFilterChain(targetObj, targetMethod);
            RsfFilter[] rsfFilters = this.rsfContext.getRsfFilters(metaData);
            new InnerRsfFilterChainInterceptor(rsfFilters, rsfChain).doFilter(request, response);
        } catch (Throwable e) {
            //500 InternalServerError
            response.sendStatus(ProtocolStatus.InternalServerError, e);
            return;
        }
        //6.检测请求是否被友谊丢弃。
        if (response.isCommitted() == false) {
            response.refresh();
            return;
        }
    }
}