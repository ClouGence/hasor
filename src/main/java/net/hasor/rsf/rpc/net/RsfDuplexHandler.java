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
package net.hasor.rsf.rpc.net;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.CombinedChannelDuplexHandler;
/**
 * @version : 2017年01月16日
 * @author 赵永春(zyc@hasor.net)
 */
@ChannelHandler.Sharable
public final class RsfDuplexHandler extends CombinedChannelDuplexHandler {
    public RsfDuplexHandler(ChannelInboundHandler inBoundArray, ChannelOutboundHandler outBoundArray) {
        super(inBoundArray, outBoundArray);
    }
}