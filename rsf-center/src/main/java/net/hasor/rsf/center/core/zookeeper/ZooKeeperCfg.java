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
package net.hasor.rsf.center.core.zookeeper;
import java.io.File;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.rsf.utils.NetworkUtils;
/**
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperCfg {
    private String workDir;
    private String dataDir;
    private String snapDir;
    private int    tickTime;
    private int    minSessionTimeout;
    private int    maxSessionTimeout;
    private String bindAddress;
    private int    bindPort;
    private int    clientCnxns;
    private int    clientTimeout;
    private String zkServers;
    //
    public ZooKeeperCfg(Environment env) {
        Settings settings = env.getSettings();
        this.workDir = env.getWorkSpaceDir();
        this.dataDir = new File(workDir, "data").getAbsolutePath();
        this.snapDir = new File(workDir, "snap").getAbsolutePath();
        this.tickTime = settings.getInteger("rsfCenter.zooKeeper.tickTime", 1000);
        this.minSessionTimeout = settings.getInteger("rsfCenter.zooKeeper.minSessionTimeout", 4000);
        this.maxSessionTimeout = settings.getInteger("rsfCenter.zooKeeper.maxSessionTimeout", 15000);//
        this.bindAddress = settings.getInteger("rsfCenter.zooKeeper.bindAddress", NetworkUtils.localHost());//绑定的端口
        this.bindPort = settings.getInteger("rsfCenter.zooKeeper.bindPort", 2180);//绑定的端口
        this.clientCnxns = settings.getInteger("rsfCenter.zooKeeper.clientCnxns", 100);//最大客户端连接数
        this.clientTimeout = settings.getInteger("rsfCenter.zooKeeper.clientTimeout", 15000);
        this.zkServers = settings.getString("rsfCenter.zooKeeper.zkServers");
    }
    //
    public String getWorkDir() {
        return workDir;
    }
    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }
    public String getDataDir() {
        return dataDir;
    }
    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }
    public String getSnapDir() {
        return snapDir;
    }
    public void setSnapDir(String snapDir) {
        this.snapDir = snapDir;
    }
    public int getTickTime() {
        return tickTime;
    }
    public void setTickTime(int tickTime) {
        this.tickTime = tickTime;
    }
    public int getMinSessionTimeout() {
        return minSessionTimeout;
    }
    public void setMinSessionTimeout(int minSessionTimeout) {
        this.minSessionTimeout = minSessionTimeout;
    }
    public int getMaxSessionTimeout() {
        return maxSessionTimeout;
    }
    public void setMaxSessionTimeout(int maxSessionTimeout) {
        this.maxSessionTimeout = maxSessionTimeout;
    }
    public String getBindAddress() {
        return bindAddress;
    }
    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }
    public int getBindPort() {
        return bindPort;
    }
    public void setBindPort(int bindPort) {
        this.bindPort = bindPort;
    }
    public int getClientCnxns() {
        return clientCnxns;
    }
    public void setClientCnxns(int clientCnxns) {
        this.clientCnxns = clientCnxns;
    }
    public int getClientTimeout() {
        return clientTimeout;
    }
    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }
    public String getZkServers() {
        return zkServers;
    }
    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }
}