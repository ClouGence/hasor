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
package net.hasor.rsf.protocol.hprose;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
/**
 * Hprose 编码器，支持将{@link RequestInfo}或者{@link ResponseInfo}编码写入Socket
 * @version : 2017年1月26日
 * @author 赵永春(zyc@hasor.net)
 */
public class HproseEncoder extends MessageToByteEncoder<Object> {
    private RsfEnvironment rsfEnvironment;
    public HproseEncoder(RsfContext rsfContext) {
        this.rsfEnvironment = rsfContext.getEnvironment();
    }
    //
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        try {
            if (msg instanceof RequestInfo) {
                RequestInfo info = (RequestInfo) msg;
                return;
            }
            if (msg instanceof ResponseInfo) {
                ResponseInfo info = (ResponseInfo) msg;
                return;
            }
        } finally {
            ctx.flush();
        }
    }
}