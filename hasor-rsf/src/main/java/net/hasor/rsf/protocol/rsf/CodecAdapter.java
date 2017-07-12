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
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.protocol.rsf.v1.RequestBlock;
import net.hasor.rsf.protocol.rsf.v1.ResponseBlock;

import java.io.IOException;
/**
 * Protocol Interface,for custom network protocol
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public interface CodecAdapter {
    /**将{@link RequestInfo},转换为{@link RequestBlock}。*/
    RequestBlock buildRequestBlock(RequestInfo info);

    /**将{@link RequestBlock}写入{@link ByteBuf}。*/
    void wirteRequestBlock(RequestBlock block, ByteBuf out) throws IOException;

    /**将{@link ByteBuf} 中读取{@link RequestInfo}信息。*/
    RequestInfo readRequestInfo(ByteBuf frame) throws IOException;

    /**将{@link ResponseInfo},转换为{@link ResponseBlock}。*/
    ResponseBlock buildResponseBlock(ResponseInfo info);

    /**将{@link ResponseBlock}写入{@link ByteBuf}。*/
    void wirteResponseBlock(ResponseBlock block, ByteBuf out) throws IOException;

    /**将{@link ByteBuf} 中读取{@link ResponseInfo}信息。*/
    ResponseInfo readResponseInfo(ByteBuf frame) throws IOException;
}