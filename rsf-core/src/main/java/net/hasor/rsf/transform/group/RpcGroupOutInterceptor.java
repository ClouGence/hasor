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
package net.hasor.rsf.transform.group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
/**
 * 截获：传出的数据包
 * @version : 2014年10月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class RpcGroupOutInterceptor extends ChannelOutboundHandlerAdapter {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("@@@@@@@@@@@@@@@");
        super.handlerAdded(ctx);
    }
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // TODO Auto-generated method stub
        super.write(ctx, msg, promise);
    }
}