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
import net.hasor.core.*;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.rpc.net.RsfChannel;
import net.hasor.rsf.rpc.net.RsfNetManager;
import net.hasor.rsf.rpc.net.RsfReceivedListener;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class NetworkTest extends RsfReceivedListener implements Provider<RsfEnvironment> {
    private RsfEnvironment rsfEnv;
    @Override
    public RsfEnvironment get() {
        return this.rsfEnv;
    }
    @Test
    public void sendPack() throws IOException, InterruptedException, ExecutionException {
        AppContext appContext = Hasor.create().putData("RSF_ENABLE", "false").build(new Module() {
            @Override
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                apiBinder.bindType(RsfEnvironment.class).toProvider(NetworkTest.this);//
            }
        });
        this.rsfEnv = new DefaultRsfEnvironment(appContext.getEnvironment());
        RsfNetManager rsfNetManager = new RsfNetManager(this.rsfEnv, this);
        rsfNetManager.start(appContext);
        System.out.println(">>>>>>>>> server started. <<<<<<<<<<");
        //
        Thread.sleep(2000);
        InterAddress local = rsfNetManager.findConnector("rsf").getBindAddress();
        RsfChannel channel = rsfNetManager.getChannel(local).get();
        for (int i = 0; i <= 10; i++) {
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
            outRequest.addParameter("java.lang.Object", "aaaa".getBytes(), null);
            System.out.println("sendData[Request] >>>>>>>>> " + outRequest.getRequestID());
            channel.sendData(outRequest, null);
        }
        //
        rsfNetManager.shutdown();
    }
    @Override
    public void receivedMessage(InterAddress form, ResponseInfo response) {
        //
        System.out.println("receivedMessage[Response] >>>>>>>>> " + response.getRequestID());
    }
    @Override
    public void receivedMessage(InterAddress form, RequestInfo request) {
        //
        System.out.println("receivedMessage[Request] >>>>>>>>> " + request.getRequestID());
    }
}