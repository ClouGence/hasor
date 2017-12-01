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
import net.hasor.core.AppContext;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.rpc.net.ConnectionAccepter;
import net.hasor.rsf.rpc.net.LinkType;
import net.hasor.rsf.rpc.net.ReceivedListener;
import net.hasor.rsf.rpc.net.RsfChannel;
import net.hasor.rsf.rpc.net.netty.NettyConnector;
import net.hasor.rsf.rpc.net.netty.ProtocolHandlerFactory;
import net.hasor.utils.future.BasicFuture;
/**
 * HTTP协议连接器。
 * @version : 2017年11月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class HttpConnector extends NettyConnector {
    private HttpHandler httpHandler;
    public HttpConnector(String protocol, AppContext appContext, ReceivedListener receivedListener, ConnectionAccepter accepter) throws ClassNotFoundException {
        super(protocol, appContext, receivedListener, accepter);
    }
    protected HttpHandler getHttpHandler() {
        return this.httpHandler;
    }
    @Override
    public void startListener(AppContext appContext) throws Throwable {
        String configKey = getRsfEnvironment().getSettings().getProtocolConfigKey(this.getProtocol());
        String httpHandlerFactory = getRsfEnvironment().getSettings().getString(configKey + ".httpHandlerFactory");
        String contextPath = getRsfEnvironment().getSettings().getString(configKey + ".contextPath");
        Class<HttpHandlerFactory> handlerClass = (Class<HttpHandlerFactory>) appContext.getClassLoader().loadClass(httpHandlerFactory);
        this.httpHandler = appContext.getInstance(handlerClass).newHandler(contextPath, this, appContext);
        super.startListener(appContext);
    }
    @Override
    public void shutdownListener() {
        this.httpHandler = null;
        super.shutdownListener();
    }
    protected ProtocolHandlerFactory createHandler(String protocol, AppContext appContext) throws ClassNotFoundException {
        return new HttpProtocolHandler(this.httpHandler);
    }
    @Override
    public void connectionTo(InterAddress hostAddress, BasicFuture<RsfChannel> channelFuture) {
        // 不会真实的去连接，只有当发起调用时才会进行http连接。因此这个阶段只需要创建 RsfChannelOnHttp 即可。
        logger.info("connect to {} ...", hostAddress.toHostSchema());
        channelFuture.completed(new RsfChannelOnHttp(hostAddress, LinkType.Out, this));
    }
}