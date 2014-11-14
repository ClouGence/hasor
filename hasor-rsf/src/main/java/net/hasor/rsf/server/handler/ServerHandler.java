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
package net.hasor.rsf.server.handler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import net.hasor.rsf.context.RsfContext;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.general.RsfException;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.net.netty.NetworkChanne;
import net.hasor.rsf.protocol.message.RequestMsg;
import net.hasor.rsf.protocol.message.ResponseMsg;
import net.hasor.rsf.protocol.toos.TransferUtils;
import net.hasor.rsf.server.RsfFilter;
import net.hasor.rsf.server.RsfFilterChain;
import net.hasor.rsf.server.RsfRequest;
import net.hasor.rsf.server.RsfResponse;
/**
 * 提供服务的Handler（只处理RequestMsg）
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private RsfContext rsfContext = null;
    //
    public ServerHandler(RsfContext rsfContext) {
        this.rsfContext = rsfContext;
    }
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RequestMsg == false)
            return;
        RequestMsg requestMsg = (RequestMsg) msg;
        requestMsg.setReceiveTime(System.currentTimeMillis());
        //
        try {
            Executor exe = this.rsfContext.getCallExecute(requestMsg.getServiceName());
            exe.execute(new InvokeHandler(this.rsfContext, requestMsg, ctx.channel()));
        } catch (RejectedExecutionException e) {
            ResponseMsg pack = TransferUtils.buildStatus(//
                    requestMsg.getVersion(), //协议版本
                    requestMsg.getRequestID(),//请求ID
                    ProtocolStatus.ChooseOther);//服务器资源紧张
            ctx.pipeline().writeAndFlush(pack);
        }
    }
    //
    /**调用主逻辑*/
    private static class InvokeHandler implements Runnable {
        private RsfContext rsfContext = null;
        private RequestMsg requestMsg = null;
        private Channel    channel    = null;
        //
        public InvokeHandler(RsfContext rsfContext, RequestMsg requestMsg, Channel channel) {
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
    /**负责处理RsfFilter调用。*/
    private static class InnerRsfFilterChainInterceptor implements RsfFilterChain {
        private RsfFilter[]    rsfFilters = null;
        private RsfFilterChain rsfChain   = null;
        private int            index      = -1;
        //
        public InnerRsfFilterChainInterceptor(final RsfFilter[] rsfFilters, final RsfFilterChain rsfChain) {
            this.rsfFilters = (rsfFilters == null) ? new RsfFilter[0] : rsfFilters;
            this.rsfChain = rsfChain;
        }
        public void doFilter(RsfRequest request, RsfResponse response) throws Throwable {
            this.index++;
            if (this.index < this.rsfFilters.length) {
                this.rsfFilters[this.index].doFilter(request, response, this);
            } else {
                this.rsfChain.doFilter(request, response);
            }
        }
    }
    /**负责执行Rsf调用，并写入response。*/
    private static class InnerRsfFilterChain implements RsfFilterChain {
        private Object targetObj    = null;
        private Method targetMethod = null;
        //
        public InnerRsfFilterChain(Object targetObj, Method targetMethod) {
            this.targetObj = targetObj;
            this.targetMethod = targetMethod;
        }
        //default invoke
        public void doFilter(RsfRequest request, RsfResponse response) throws Throwable {
            if (response.isCommitted() == true)
                return;
            Object[] params = request.getParameterObject();
            Object resData = this.targetMethod.invoke(this.targetObj, params);
            response.sendData(resData);
        }
    }
}