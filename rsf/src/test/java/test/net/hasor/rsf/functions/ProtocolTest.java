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
package test.net.hasor.rsf.functions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.hasor.core.Hasor;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.protocol.rsf.v1.CodecAdapterForV1;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import org.junit.Test;

import java.io.IOException;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProtocolTest {
    @Test
    public void requestPack() throws IOException {
        DefaultRsfEnvironment rsfEnv = new DefaultRsfEnvironment(Hasor.createAppContext().getEnvironment());
        CodecAdapterForV1 codecAdapter = new CodecAdapterForV1(rsfEnv);
        //
        //
        RequestInfo outRequest = new RequestInfo();
        outRequest.setMessage(true);
        outRequest.setClientTimeout(1000);
        outRequest.setReceiveTime(System.nanoTime());
        outRequest.setRequestID(System.currentTimeMillis());
        outRequest.setSerializeType("json");
        outRequest.setServiceGroup("Test");
        outRequest.setServiceName("java.util.List");
        outRequest.setServiceVersion("1.0.0");
        outRequest.setTargetMethod("add");
        outRequest.addParameter("java.lang.Object", "aaaa".getBytes(), null);
        //
        ByteBuf outBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        codecAdapter.wirteRequestBlock(codecAdapter.buildRequestBlock(outRequest), outBuf);
        byte[] datas = outBuf.array();
        //
        //
        ByteBuf inBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        inBuf.writeBytes(datas);
        RequestInfo inRequest = codecAdapter.readRequestInfo(inBuf);
        //
        System.out.println(inRequest);
    }
    //
    @Test
    public void responsePack() throws IOException {
        DefaultRsfEnvironment rsfEnv = new DefaultRsfEnvironment(Hasor.createAppContext().getEnvironment());
        CodecAdapterForV1 codecAdapter = new CodecAdapterForV1(rsfEnv);
        //
        ResponseInfo outResponse = new ResponseInfo();
        outResponse.setSerializeType("json");
        outResponse.setRequestID(System.currentTimeMillis());
        outResponse.setReceiveTime(System.currentTimeMillis());
        outResponse.setReturnData("ok".getBytes());
        outResponse.setStatus((short) 200);
        //
        ByteBuf outBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        codecAdapter.wirteResponseBlock(codecAdapter.buildResponseBlock(outResponse), outBuf);
        byte[] datas = outBuf.array();
        //
        //
        ByteBuf inBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        inBuf.writeBytes(datas);
        ResponseInfo inResponse = codecAdapter.readResponseInfo(inBuf);
        //
        System.out.println(inResponse);
    }
}