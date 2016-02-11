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
package test.net.hasor.rsf._07_network;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.domain.RsfRuntimeUtils;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.rpc.net.ReceivedListener;
import net.hasor.rsf.rpc.net.RsfNetChannel;
import net.hasor.rsf.rpc.net.RsfNetManager;
import net.hasor.rsf.rpc.net.SendCallBack;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseInfo;
/**
 * 
 * @version : 2015年12月13日
 * @author 赵永春(zyc@hasor.net)
 */
public class NetworkTest implements ReceivedListener {
    protected Logger   logger    = LoggerFactory.getLogger(getClass());
    private AtomicLong sendCount = new AtomicLong(0);
    private long       startTime = System.currentTimeMillis();
    private long       lastTime  = System.currentTimeMillis();
    public void printInfo(long rtTime) {
        long checkTime = System.currentTimeMillis();
        if (checkTime - startTime == 0) {
            return;
        }
        //
        if (checkTime - lastTime < 1000) {
            return;//10秒打印一条
        }
        lastTime = System.currentTimeMillis();
        long qpsSecnd = (sendCount.get() / ((checkTime - startTime) / 1000));
        logger.info("count:{} , QPS:{} , RT:{}", sendCount, qpsSecnd, rtTime);
        //
        /*1000亿次调用之后重置统计数据*/
        if (sendCount.get() >= 100000000L) {
            sendCount.set(0);
            startTime = System.currentTimeMillis() / 1000;
            lastTime = System.currentTimeMillis();
        }
    }
    //
    //
    private RsfNetManager server() throws IOException, URISyntaxException {
        final AppContext appContext = Hasor.createAppContext("07_server-config.xml");
        final RsfEnvironment environment = new DefaultRsfEnvironment(appContext.getEnvironment());//create RsfEnvironment
        RsfNetManager server = new RsfNetManager(environment, this);
        server.start();
        return server;
    }
    private RsfNetManager client() throws IOException, URISyntaxException {
        final AppContext appContext = Hasor.createAppContext("07_client-config.xml");
        final RsfEnvironment environment = new DefaultRsfEnvironment(appContext.getEnvironment());//create RsfEnvironment
        RsfNetManager client = new RsfNetManager(environment, this);
        client.start();
        return client;
    }
    private RequestInfo buildInfo() throws Throwable {
        RequestInfo request = new RequestInfo();
        request.setRequestID(RsfRuntimeUtils.genRequestID());
        request.setServiceGroup("RSF");
        request.setServiceName("test.net.hasor.rsf.services.EchoService");
        request.setServiceVersion("1.0.0");
        request.setSerializeType("serializeType");
        request.setTargetMethod("targetMethod");
        request.setClientTimeout(6000);
        request.setReceiveTime(System.currentTimeMillis());
        //
        //        request.addParameter("java.lang.String", coder.encode("say Hello."));
        //        request.addParameter("java.lang.Long", coder.encode(111222333444555L));
        //        request.addParameter("java.util.Date", coder.encode(new Date()));
        //        request.addParameter("java.lang.Object", coder.encode(null));
        //
        request.addOption("auth", "yes");
        request.addOption("user", "guest");
        request.addOption("password", null);
        return request;
    }
    //
    private RsfNetManager server;
    private RsfNetManager client;
    private InterAddress  local;
    private void sendData(RsfNetManager client, SendCallBack callBack) throws Throwable {
        sendCount.getAndIncrement();
        long startTime = System.currentTimeMillis();
        {
            RsfNetChannel toServerChannel = client.getChannel(local).get();
            toServerChannel.sendData(buildInfo(), callBack);
        }
        printInfo(System.currentTimeMillis() - startTime);
    }
    @Test()
    public void testNetwork() throws Throwable {
        server = server();
        client = client();
        local = new InterAddress("127.0.0.1", 8000, "local");
        //
        while (true) {
            long checkTime = System.currentTimeMillis();
            if (checkTime - startTime > 120000) {
                return;//120秒退出
            }
            sendData(client, null);
        }
    }
    @Override
    public void receivedMessage(InterAddress form, ResponseInfo response) {
        //System.out.println("received ResponseInfo:" + response.getRequestID());
    }
    @Override
    public void receivedMessage(InterAddress form, RequestInfo response) {
        //System.out.println("received RequestInfo:" + response.getRequestID());
    }
}