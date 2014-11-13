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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import net.hasor.rsf.context.RsfContext;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.metadata.ServiceMetaData;
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
        RequestMsg request = (RequestMsg) msg;
        request.setReceiveTime(System.currentTimeMillis());
        //
        try {
            Executor exe = this.rsfContext.getCallExecute(request.getServiceName());
            exe.execute(new InvokeHandler(this.rsfContext, request, ctx));
        } catch (RejectedExecutionException e) {
            ResponseMsg pack = TransferUtils.buildStatus(//
                    request.getVersion(), //协议版本
                    request.getRequestID(),//请求ID
                    ProtocolStatus.ChooseOther);//服务器资源紧张
            ctx.pipeline().writeAndFlush(pack);
        }
    }
    //
    //
    //
    /**调用主逻辑*/
    private static class InvokeHandler implements Runnable {
        private RsfContext            rsfContext     = null;
        private RequestMsg            requestMessage = null;
        private ChannelHandlerContext channelContext = null;
        //
        public InvokeHandler(RsfContext rsfContext, RequestMsg requestMessage, ChannelHandlerContext channelContext) {
            this.rsfContext = rsfContext;
            this.requestMessage = requestMessage;
            this.channelContext = channelContext;
        }
        public void run() {
            RsfRequestImpl request = new RsfRequestImpl(requestMessage, channelContext, rsfContext);
            RsfResponseImpl response = new RsfResponseImpl(request, channelContext, rsfContext);
            this.process(request, response);
        }
        private void process(RsfRequestImpl request, RsfResponseImpl response) {
            //
            //1.check 404(NotFound)
            String serviceName = request.getMetaData().getServiceName();
            ServiceMetaData metaData = this.rsfContext.getService(serviceName);
            if (metaData == null) {
                response.sendStatus(ProtocolStatus.NotFound, "service was not found.");
                return;
            }
            //
            //2.引发反序列化参数
            try {
                request.init();
            } catch (Throwable e) {
                //503 SerializeError
                response.sendStatus(ProtocolStatus.SerializeError, e);
                return;
            }
            //
            //3.获取调用对象
            Object targetObj = null;
            Method targetMethod = null;
            Object forbidden = null;
            try {
                targetObj = this.rsfContext.getBean(metaData);
                if (targetObj != null) {
                    targetMethod = targetObj.getClass().getMethod(//
                            request.getMethod(), request.getParameterTypes());
                }
                if (targetObj == null || targetMethod == null) {
                    forbidden = "failed to get the instance.";
                }
            } catch (Exception e) {
                forbidden = e;
            }
            if (forbidden != null) {
                //403 Forbidden
                response.sendStatus(ProtocolStatus.Forbidden, forbidden);
                return;
            }
            //
            //4.检查timeout
            long lostTime = System.currentTimeMillis() - request.getReceiveTime();
            if (lostTime > request.getTimeout()) {
                response.sendStatus(ProtocolStatus.RequestTimeout, "request timeout. (client parameter).");
                return;
            }
            //
            //5.执行调用
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
                response.sendStatus(ProtocolStatus.Ignore);
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