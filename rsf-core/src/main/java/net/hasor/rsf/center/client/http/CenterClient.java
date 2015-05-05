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
package net.hasor.rsf.center.client.http;
import java.util.HashMap;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
/***
 * 
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class CenterClient extends Thread {
    private final int        centerInterval;
    private final HttpClient httpClient;
    public CenterClient(AbstractRsfContext rsfContext) {
        this.httpClient = new HttpClient(rsfContext);
        this.centerInterval = rsfContext.getSettings().getCenterInterval();
        this.setDaemon(true);
        this.setName("CenterClient-[Beat=" + getCenterInterval() + "]");
    }
    /**获取心跳时间*/
    public int getCenterInterval() {
        return this.centerInterval;
    }
    //
    /**终端上线*/
    public void onLine() throws Throwable {
        this.httpClient.request("/apis/online", new HashMap<String, String>());
    }
    /**终端下线*/
    public void offLine() throws Throwable {
        this.httpClient.request("/apis/offline", new HashMap<String, String>());
    }
    //
    /**服务注册*/
    public void registryService() throws Throwable {
        this.httpClient.request("/apis/offline", new HashMap<String, String>());
    }
    /**服务解除注册*/
    public void unRegistryService() throws Throwable {
        this.httpClient.request("/apis/offline", new HashMap<String, String>());
    }
}