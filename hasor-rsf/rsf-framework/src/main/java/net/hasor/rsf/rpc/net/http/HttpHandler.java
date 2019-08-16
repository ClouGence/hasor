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
package net.hasor.rsf.rpc.net.http;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;

import java.io.IOException;

/**
 * Http 解码器组
 * @version : 2017年11月22日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface HttpHandler {
    /**
     * 接收到 http request 请求并做出响应。
     * 1. 可以解析 httpRequest，并生成 RequestInfo 对象。然后 outputTo.callRPC 进行服务调用。
     * 2. 或者直接把rpc结果写到 RsfHttpResponse 的输出流中，然后直接 outputTo.finishRPC 完成响应。
     */
    public void receivedRequest(RsfHttpRequest httpRequest, RsfHttpResponse httpResponse, HttpResult outputTo) throws Throwable;

    /** 结果处理方式 */
    public static interface HttpResult {
        /**
         * 方式一：解析 httpRequest，并生成 RequestInfo 对象，然后让 rsf 调度这个请求。
         * @throws IllegalStateException callRPC、finishRPC 两个方法中只有一个能被成功调用，且只能调用一次。
         */
        public void callRPC(RequestInfo requestInfo, ResponseEncoder encoder);

        /**
         * 方式二：结束 RPC 调用立即进行 response 响应。
         * @throws IllegalStateException callRPC、finishRPC 两个方法中只有一个能被成功调用，且只能调用一次。
         */
        public void finishRPC();
    }

    /** Response 编码器 */
    public static interface ResponseEncoder {
        /** 发生异常 */
        public void exception(RsfHttpResponse httpResponse, Throwable e) throws IOException;

        /** 完成调用 */
        public void complete(RsfHttpResponse httpResponse, ResponseInfo info) throws IOException;
    }
    //
    // ------------------------------------------------------------------------------------------------------
    //

    /**
     * 准备向外发送 http request 请求。
     * 1. 可以解析 RequestInfo，并生成 RequestObject 对象。然后通过 builder.sendRequest 进行服务调用。
     * 2. 或者直接把rpc结果写到 builder.finishRequest 中。
     */
    public void sendRequest(InterAddress requestHost, RequestInfo info, SenderBuilder builder) throws Throwable;

    /** Response 编码器 */
    public static interface SenderBuilder {
        /**
         * 方式一：发送调用请求。
         * @throws IllegalStateException sendRequest、finishRequest 两个方法中只有一个能被成功调用，且只能调用一次。
         */
        public void sendRequest(RequestObject httpRequest, ResponseDecoder decoder);

        /**
         * 方式二：直接给出请求结果。
         * @throws IllegalStateException sendRequest、finishRequest 两个方法中只有一个能被成功调用，且只能调用一次。
         */
        public void finishRequest(ResponseInfo responseInfo) throws IOException;
    }

    /** Response 解码器 */
    public static interface ResponseDecoder {
        /** 完成调用 */
        public ResponseInfo complete(long requestID, RsfHttpResponseData httpResponse) throws IOException;
    }
}