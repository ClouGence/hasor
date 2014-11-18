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
package net.hasor.rsf.runtime.common;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
/**
 * 
 * @version : 2014年11月14日
 * @author 赵永春(zyc@hasor.net)
 */
public class NetworkConnection {
    private String  remoteHost   = null;
    private int     remotePort   = 0;
    private String  localHost    = null;
    private int     localPort    = 0;
    private Channel socketChanne = null;
    //
    public NetworkConnection(Channel socketChanne) {
        this.socketChanne = socketChanne;
        //remote
        SocketAddress rAddress = socketChanne.remoteAddress();//InetSocketAddress
        if (rAddress instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) rAddress;
            this.remoteHost = address.getAddress().getHostAddress();
            this.remotePort = address.getPort();
        }
        //local
        SocketAddress lAddress = socketChanne.localAddress();//InetSocketAddress
        if (lAddress instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) lAddress;
            this.localHost = address.getAddress().getHostAddress();
            this.localPort = address.getPort();
        }
    }
    //
    /**远程IP（如果远程使用了代理服务器那么该IP将不可信）。*/
    public String getRemotHost() {
        return this.remoteHost;
    }
    /**远程端口。*/
    public int getRemotePort() {
        return this.remotePort;
    }
    /**本地IP。*/
    public String getLocalHost() {
        return this.localHost;
    }
    /**本地端口。*/
    public int getLocalPort() {
        return this.localPort;
    }
    /**连接是否为活动的。*/
    public boolean isActive() {
        return this.socketChanne.isActive();
    }
    /**关闭连接。*/
    public Future<Void> close() {
        return this.socketChanne.close();
    }
    /**获取具体的连接。*/
    public Channel getChannel() {
        return this.socketChanne;
    }
}