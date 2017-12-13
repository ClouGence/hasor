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
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.hasor.core.*;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.domain.OptionInfo;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.rpc.net.ConnectionAccepter;
import net.hasor.rsf.rpc.net.Connector;
import net.hasor.rsf.rpc.net.ReceivedListener;
import net.hasor.rsf.rpc.net.RsfChannel;
import net.hasor.rsf.rpc.net.netty.NettyConnectorFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.Future;
/**
 *  在 Connector 层面上测试，启动本地监听服务，并且连接到远程连接器上进行数据发送和接收。
 * @version : 2014年9月12日
 * @author 赵永春 (zyc@hasor.net)
 */
public class ConnectorTest extends ChannelInboundHandlerAdapter implements Provider<RsfEnvironment>, ReceivedListener {
    private RsfEnvironment rsfEnv;
    @Override
    public RsfEnvironment get() {
        return this.rsfEnv;
    }
    @Test
    public void sendPack() throws Throwable {
        AppContext appContext = Hasor.create().putData("RSF_ENABLE", "false").build(new Module() {
            @Override
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                apiBinder.bindType(RsfEnvironment.class).toProvider(ConnectorTest.this);
            }
        });
        this.rsfEnv = new DefaultRsfEnvironment(appContext.getEnvironment());
        String protocolKey = "RSF/1.0";
        Connector connector = new NettyConnectorFactory().create(protocolKey, appContext, this, new ConnectionAccepter() {
            @Override
            public boolean acceptIn(RsfChannel rsfChannel) throws IOException {
                return true;
            }
        });
        connector.startListener(appContext);
        System.out.println(">>>>>>>>> server started. <<<<<<<<<<");
        //
        Thread.sleep(2000);
        Future<RsfChannel> result = connector.getOrConnectionTo(connector.getBindAddress());
        RsfChannel rsfChannel = result.get();
        for (int i = 0; i <= 10; i++) {
            Thread.sleep(1);
            RequestInfo outRequest = new RequestInfo();
            outRequest.setMessage(i % 2 == 0);
            outRequest.setClientTimeout(1000);
            outRequest.setReceiveTime(System.nanoTime());
            outRequest.setRequestID(System.currentTimeMillis());
            outRequest.setSerializeType("json");
            outRequest.setServiceGroup("Test");
            outRequest.setServiceName("java.util.List");
            outRequest.setServiceVersion("1.0.0");
            outRequest.setTargetMethod("add");
            outRequest.addParameter("java.lang.Object", "aaaa");
            System.out.println("sendData[Request] >>>>>>>>> " + outRequest.getRequestID());
            rsfChannel.sendData(outRequest, null);
        }
        //
        Thread.sleep(2000);
        System.out.println(">>>>>>>>> stop. <<<<<<<<<<");
        rsfChannel.close();
        connector.shutdown();
    }
    //
    @Override
    public void receivedMessage(RsfChannel rsfChannel, OptionInfo info) {
        if (info instanceof RequestInfo) {
            System.out.println("receivedMessage[Request] >>>>>>>>> " + ((RequestInfo) info).getRequestID());
        }
        if (info instanceof ResponseInfo) {
            System.out.println("receivedMessage[Response] >>>>>>>>> " + ((ResponseInfo) info).getRequestID());
        }
    }
}