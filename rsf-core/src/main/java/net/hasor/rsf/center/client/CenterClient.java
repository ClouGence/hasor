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
package net.hasor.rsf.center.client;
import java.util.HashMap;
import net.hasor.core.EventListener;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.rpc.event.Events;
import org.more.logger.LoggerHelper;
/***
 * 
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class CenterClient extends Thread implements EventListener {
    private final int        centerInterval;
    private final HttpClient httpClient;
    private boolean          online;
    public CenterClient(AbstractRsfContext rsfContext) {
        this.httpClient = new HttpClient(rsfContext);
        this.centerInterval = rsfContext.getSettings().getCenterInterval();
        this.setDaemon(true);
        this.setName("CenterClient-[Beat=" + getCenterInterval() + "]");
        this.online = false;
    }
    /**获取心跳时间*/
    public int getCenterInterval() {
        return this.centerInterval;
    }
    //
    //
    /**终端上线*/
    public void onLine() throws Throwable {
        this.online = true;
        this.httpClient.request("/apis/online", new HashMap<String, String>());
    }
    /**终端下线*/
    public void offLine() throws Throwable {
        this.online = false;
        this.httpClient.request("/apis/offline", new HashMap<String, String>());
    }
    /**服务消费者*/
    public void serviceCustomer() throws Throwable {
        this.httpClient.request("/apis/customer", new HashMap<String, String>());
    }
    /**服务提供者*/
    public void serviceProvider() throws Throwable {
        this.httpClient.request("/apis/provider", new HashMap<String, String>());
    }
    /**终端服务声明注销*/
    public void unService() throws Throwable {
        this.httpClient.request("/apis/unregistered", new HashMap<String, String>());
    }
    /**与注册中心的心跳*/
    public void heartbeat() throws Throwable {
        this.httpClient.request("/apis/heartbeat", new HashMap<String, String>());
    }
    //
    //
    public void run() {
        while (true) {
            try {
                if (this.online) {
                    heartbeat();
                }
            } catch (Throwable e) {
                LoggerHelper.logSevere("client heartbeat error ->", e.getMessage());
            } finally {
                try {
                    sleep(centerInterval);
                } catch (InterruptedException e) {}
            }
        }
    }
    //
    //
    public void onEvent(String event, Object[] params) throws Throwable {
        try {
            LoggerHelper.logInfo("rsf event -> " + event);
            /*  */if (Events.StartUp.equals(event)) {
                //
                this.onLine();
            } else if (Events.Shutdown.equals(event)) {
                //
                this.offLine();
            } else if (Events.ServiceCustomer.equals(event)) {
                //
                this.serviceCustomer();
            } else if (Events.ServiceProvider.equals(event)) {
                //
                this.serviceProvider();
            } else if (Events.UnService.equals(event)) {
                //
                this.unService();
            }
        } catch (Exception e) {
            LoggerHelper.logSevere(e.getMessage(), e);
        }
    }
}