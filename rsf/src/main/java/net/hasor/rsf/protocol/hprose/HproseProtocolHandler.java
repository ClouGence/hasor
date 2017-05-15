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
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import net.hasor.core.AppContext;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.rpc.net.Connector;
import net.hasor.rsf.rpc.net.ProtocolHandler;
import net.hasor.rsf.rpc.net.RsfChannel;
import net.hasor.rsf.rpc.net.RsfDuplexHandler;
/**
 * Hprose 解码器
 * @version : 2014年10月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class HproseProtocolHandler implements ProtocolHandler {
    @Override
    public boolean acceptIn(Connector connector, RsfChannel rsfChannel) {
        return rsfChannel.activeIn();
    }
    @Override
    public ChannelHandler[] channelHandler(Connector connector, AppContext appContext) {
        RsfContext rsfContext = appContext.getInstance(RsfContext.class);
        RsfDuplexHandler inHandler = new RsfDuplexHandler(  //
                new HttpRequestDecoder(),   //
                new HttpResponseEncoder()   //
        );
        RsfDuplexHandler outHandler = new RsfDuplexHandler( //
                new HttpResponseDecoder(),  //
                new HttpRequestEncoder()    //
        );
        return new ChannelHandler[] {           //
                inHandler,                      //
                new HproseHttpCoder(rsfContext) //
        };
    }
}