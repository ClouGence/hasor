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
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.protocol.message.ResponseMsg;
import net.hasor.rsf.runtime.RsfFilter;
import net.hasor.rsf.runtime.common.RsfRequestImpl;
import net.hasor.rsf.runtime.common.RsfResponseImpl;
import net.hasor.rsf.runtime.context.AbstractRsfContext;
import net.hasor.rsf.serialize.SerializeFactory;
import org.more.util.BeanUtils;
/**
 * 负责处理 Request 调用逻辑，和response写入逻辑。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerRequestHandler implements Runnable {
    private AbstractRsfContext rsfContext;
    private RsfRequestImpl     request  = null;
    private RsfResponseImpl    response = null;
    //
    public InnerRequestHandler(AbstractRsfContext rsfContext, RsfRequestImpl request, RsfResponseImpl response) {
        this.rsfContext = rsfContext;
        this.request = request;
        this.response = response;
    }
    public void run() {
        RsfResponseImpl response = this.doRequest();
        sendResponse(response);
    }
    private RsfResponseImpl doRequest() {
        //1.检查timeout
        long lostTime = System.currentTimeMillis() - this.request.getReceiveTime();
        if (lostTime > this.request.getTimeout()) {
            response.sendStatus(ProtocolStatus.RequestTimeout, "request timeout. (client parameter).");
            return response;
        }
        //2.执行调用
        try {
            RsfFilter[] rsfFilters = this.rsfContext.getRsfFilters(request.getMetaData());
            new InnerRsfFilterHandler(rsfFilters, InnerInvokeHandler.Default).doFilter(request, response);
        } catch (Throwable e) {
            //500 InternalServerError
            response.sendStatus(ProtocolStatus.InternalServerError, e.getMessage());
            return response;
        }
        return response;
    }
    private void sendResponse(RsfResponseImpl response) {
        //给予默认值
        if (response.isResponse() == false) {
            Object defaultValue = BeanUtils.getDefaultValue(response.getResponseType());
            response.sendData(defaultValue);
        }
        //回写Socket
        ResponseMsg responseMsg = response.getMsg();
        try {
            Object responseData = response.getResponseData();
            if (responseData != null) {
                SerializeFactory serializeFactory = this.rsfContext.getSerializeFactory();
                responseMsg.setReturnData(responseData, serializeFactory);
            }
        } catch (Throwable e) {
            String msg = e.getClass().getName() + ":" + e.getMessage();
            responseMsg.setStatus(ProtocolStatus.SerializeError);;
            responseMsg.setReturnData(msg.getBytes());;
            responseMsg.setReturnType(String.class.getName());
        }
        this.request.getConnection().getChannel().writeAndFlush(responseMsg);
    }
}