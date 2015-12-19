/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.plugins.center;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.future.BasicFuture;
import org.more.util.ResourcesUtils;
import org.more.util.StringUtils;
import org.more.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.handler.codec.http.HttpResponse;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
/***
 * 
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class CenterClient extends Thread {
    protected Logger     logger = LoggerFactory.getLogger(getClass());
    private RsfContext   rsfContext;
    private RsfClient    rsfClient;
    private final int    centerInterval;
    private boolean      online;
    private String       terminalID;
    private String       terminalAccessKey;
    private InterAddress centerAddress;
    //
    public CenterClient(AbstractRsfContext rsfContext, InterAddress centerAddress) throws UnknownHostException {
        this.centerInterval = rsfContext.getSettings().getCenterInterval();
        this.rsfContext = rsfContext;
        this.rsfClient = rsfContext.getRsfClient(centerAddress);
        this.online = false;
        this.centerAddress = centerAddress;
        this.setDaemon(true);
        this.setName("CenterClient-[Beat=" + getCenterInterval() + "]");
    }
    //
    /**获取心跳时间*/
    public int getCenterInterval() {
        return this.centerInterval;
    }
    //
    public void run() {
        while (true) {
            try {
                if (this.online) {
                    heartbeat();
                }
            } catch (Throwable e) {
                logger.error("client heartbeat error ->", e.getMessage());
            } finally {
                try {
                    sleep(centerInterval);
                } catch (InterruptedException e) {/**/}
            }
        }
    }
    //
    /**终端上线*/
    public String onLine(String terminalAccessKey, String hostName,) throws Throwable {
        Map<String, String> reqParam = new HashMap<String, String>();
        reqParam.put(CenterParams.Terminal_HostName, this.centerAddress.getHost());
        reqParam.put(CenterParams.Terminal_HostPort, String.valueOf(this.centerAddress.getHostPort()));
        reqParam.put(CenterParams.Terminal_HostUnit, String.valueOf(this.rsfContext.getSettings().getUnitName()));
        reqParam.put(CenterParams.Terminal_Version, IOUtils.toString(ResourcesUtils.getResourceAsStream("/META-INF/rsf-core.version")));
        //
        BasicFuture<HttpResponse> response = this.httpClient.request("/apis/online", reqParam);
        this.terminalID = response.get().headers().get(CenterParams.Terminal_ID);
        this.terminalAccessKey = response.get().headers().get(CenterParams.Terminal_AccessKey);
        if (!StringUtils.isBlank(this.terminalID)) {
            logger.info("onLine to center, terminalID-> {}", this.terminalID);
            this.online = true;
        }
    }
    /**终端下线*/
    public void offLine(String terminalID, String terminalAccessKey);
    /**服务消费者*/
    public void serviceCustomer(RsfBindInfo<?> bindInfo) throws Throwable {
        Map<String, String> reqParam = new HashMap<String, String>();
        reqParam.put(CenterParams.Terminal_ID, this.terminalID);
        reqParam.put(CenterParams.Terminal_AccessKey, this.terminalAccessKey);
        reqParam.put(CenterParams.Service_BindID, bindInfo.getBindID());
        reqParam.put(CenterParams.Service_BindName, bindInfo.getBindName());
        reqParam.put(CenterParams.Service_BindGroup, bindInfo.getBindGroup());
        reqParam.put(CenterParams.Service_BindVersion, bindInfo.getBindVersion());
        reqParam.put(CenterParams.Service_BindType, bindInfo.getBindType().getName());
        reqParam.put(CenterParams.Service_ClientTimeout, String.valueOf(bindInfo.getClientTimeout()));
        reqParam.put(CenterParams.Service_SerializeType, bindInfo.getSerializeType());
        reqParam.put(CenterParams.Service_Persona, "customer");
        //
        updateAddress(this.httpClient.request("/apis/customer", reqParam));
    }
    /**服务提供者*/
    public void serviceProvider(RsfBindInfo<?> bindInfo) throws Throwable {
        Map<String, String> reqParam = new HashMap<String, String>();
        reqParam.put(CenterParams.Terminal_ID, this.terminalID);
        reqParam.put(CenterParams.Terminal_AccessKey, this.terminalAccessKey);
        reqParam.put(CenterParams.Service_BindID, bindInfo.getBindID());
        reqParam.put(CenterParams.Service_BindName, bindInfo.getBindName());
        reqParam.put(CenterParams.Service_BindGroup, bindInfo.getBindGroup());
        reqParam.put(CenterParams.Service_BindVersion, bindInfo.getBindVersion());
        reqParam.put(CenterParams.Service_BindType, bindInfo.getBindType().getName());
        reqParam.put(CenterParams.Service_ClientTimeout, String.valueOf(bindInfo.getClientTimeout()));
        reqParam.put(CenterParams.Service_SerializeType, bindInfo.getSerializeType());
        reqParam.put(CenterParams.Service_Persona, "provider");
        //
        updateAddress(this.httpClient.request("/apis/provider", reqParam));
    }
    /**终端服务声明注销*/
    public void unService(RsfBindInfo<?> bindInfo) throws Throwable {
        Map<String, String> reqParam = new HashMap<String, String>();
        reqParam.put(CenterParams.Terminal_ID, this.terminalID);
        reqParam.put(CenterParams.Terminal_AccessKey, this.terminalAccessKey);
        reqParam.put(CenterParams.Service_BindID, bindInfo.getBindID());
        //
        this.httpClient.request("/apis/unservice", reqParam);
    }
    /**与注册中心的心跳*/
    public void heartbeat() throws Throwable {
        Map<String, String> reqParam = new HashMap<String, String>();
        reqParam.put(CenterParams.Terminal_ID, this.terminalID);
        reqParam.put(CenterParams.Terminal_AccessKey, this.terminalAccessKey);
        //
        StringBuffer buffer = new StringBuffer("");
        List<String> ids = this.rsfContext.getServiceIDs();
        for (String id : ids) {
            buffer.append("," + id);
        }
        if (buffer.length() > 1) {
            buffer.deleteCharAt(0);
        }
        reqParam.put(CenterParams.HeartBeat, buffer.toString());
        //
        updateAddress(this.httpClient.request("/apis/heartbeat", reqParam));
    }
    //
    private void updateAddress(BasicFuture<HttpResponse> response) {
        //TODO 更新本地地址本
        System.out.println();
    }
}