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
import java.util.Date;
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
import net.hasor.rsf.serialize.SerializeCoder;
import net.hasor.rsf.serialize.coder.JavaSerializeCoder;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseInfo;
/**
 * 
 * @version : 2015年12月13日
 * @author 赵永春(zyc@hasor.net)
 */
public class NetworkFunTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private RequestInfo buildRequest() throws Throwable {
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
        SerializeCoder coder = new JavaSerializeCoder();
        request.addParameter("java.lang.String", coder.encode("say Hello."));
        request.addParameter("java.lang.Long", coder.encode(111222333444555L));
        request.addParameter("java.util.Date", coder.encode(new Date()));
        request.addParameter("java.lang.Object", coder.encode(null));
        //
        request.addOption("auth", "yes");
        request.addOption("user", "guest");
        request.addOption("password", null);
        return request;
    }
    private ResponseInfo buildResponse(RequestInfo request) throws Throwable {
        ResponseInfo response = new ResponseInfo();
        response.setRequestID(request.getRequestID());
        response.setSerializeType("serializeType");
        response.setReceiveTime(System.currentTimeMillis());
        //
        SerializeCoder coder = new JavaSerializeCoder();
        response.setReturnData(coder.encode(new Date()));
        //
        response.addOption("auth", "yes");
        response.addOption("user", "guest");
        response.addOption("password", null);
        return response;
    }
    //
    //
    //
    private RsfNetManager server = null;
    private RsfNetManager client = null;
    @Test()
    public void testNetworkFunTest() throws Throwable {
        final AppContext serverAppContext = Hasor.createAppContext("07_server-config.xml");
        final RsfEnvironment serverEnvironment = new DefaultRsfEnvironment(serverAppContext.getEnvironment());//create RsfEnvironment
        server = new RsfNetManager(serverEnvironment, new ReceivedListener() {
            public void receivedMessage(InterAddress form, RequestInfo response) {
                try {
                    System.out.println("[Server]received RequestInfo message.");
                    server.getChannel(form).get().sendData(buildResponse(response), null);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            public void receivedMessage(InterAddress form, ResponseInfo response) {
                System.out.println("[Server]received ResponseInfo message.");
            }
        });
        server.start();
        //
        //
        //
        final AppContext clientAppContext = Hasor.createAppContext("07_client-config.xml");
        final RsfEnvironment clientEnvironment = new DefaultRsfEnvironment(serverAppContext.getEnvironment());//create RsfEnvironment
        client = new RsfNetManager(clientEnvironment, new ReceivedListener() {
            public void receivedMessage(InterAddress form, RequestInfo response) {
                try {
                    System.out.println("[Client]received RequestInfo message.");
                    client.getChannel(form).get().sendData(buildResponse(response), null);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            public void receivedMessage(InterAddress form, ResponseInfo response) {
                System.out.println("[Client]received ResponseInfo message.");
            }
        });
        client.start();
        //
        //
        //
        RsfNetChannel clientToServer = client.getChannel(new InterAddress("127.0.0.1", 8000, "local")).get();
        clientToServer.sendData(buildRequest(), null);
        RsfNetChannel serverToClient = server.getChannel(new InterAddress("127.0.0.1", 8001, "local")).get();
        serverToClient.sendData(buildRequest(), null);
        //
        System.out.println();
    }
}