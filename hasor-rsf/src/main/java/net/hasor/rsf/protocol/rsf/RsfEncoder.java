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
package net.hasor.rsf.protocol.rsf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.rsf.protocol.rsf.v1.RequestBlock;
import net.hasor.rsf.protocol.rsf.v1.ResponseBlock;
/**
 * RSF 编码器，支持将{@link RequestInfo}、{@link RequestBlock}或者{@link ResponseInfo}、{@link ResponseBlock}编码写入Socket
 * @version : 2014年10月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfEncoder extends MessageToByteEncoder<Object> {
    private RsfEnvironment rsfEnvironment;
    public RsfEncoder(RsfEnvironment rsfEnvironment) {
        this.rsfEnvironment = rsfEnvironment;
    }
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        try {
            if (msg instanceof RequestInfo) {
                RequestInfo info = (RequestInfo) msg;
                CodecAdapter factory = CodecAdapterFactory.getCodecAdapterByVersion(this.rsfEnvironment, RsfConstants.Version_1);
                factory.wirteRequestBlock(factory.buildRequestBlock(info), out);
                return;
            }
            if (msg instanceof ResponseInfo) {
                ResponseInfo info = (ResponseInfo) msg;
                CodecAdapter factory = CodecAdapterFactory.getCodecAdapterByVersion(this.rsfEnvironment, RsfConstants.Version_1);
                factory.wirteResponseBlock(factory.buildResponseBlock(info), out);
                return;
            }
            if (msg instanceof RequestBlock) {
                RequestBlock block = (RequestBlock) msg;
                CodecAdapter factory = CodecAdapterFactory.getCodecAdapterByVersion(this.rsfEnvironment, RsfConstants.Version_1);
                factory.wirteRequestBlock(block, out);
                return;
            }
            if (msg instanceof ResponseBlock) {
                ResponseBlock block = (ResponseBlock) msg;
                CodecAdapter factory = CodecAdapterFactory.getCodecAdapterByVersion(this.rsfEnvironment, RsfConstants.Version_1);
                factory.wirteResponseBlock(block, out);
                return;
            }
        } finally {
            ctx.flush();
        }
    }
}