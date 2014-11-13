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
package net.hasor.rsf.client;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.RsfRequest;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfRequestImpl implements RsfRequest {
    private long                requestID        = 0;
    private ServiceMetaData     metaData         = null;
    private Method              targetMethod     = null;
    private Channel             socketChanne     = null;
    private RsfContext          rsfContext       = null;
    //
    private String              remoteHost       = null;
    private int                 remotePort       = 0;
    private String              localHost        = null;
    private int                 localPort        = 0;
    //
    private Class<?>[]          parameterTypes   = null;
    private  = null;
    private Map<String, String> optionMap        = null; //选项
    //
    //
    public RsfRequestImpl(long requestID, Method targetMethod,  ServiceMetaData metaData, Channel socketChanne, RsfContext rsfContext) {
        this.requestID = requestID;
        this.metaData = metaData;
        this.socketChanne = socketChanne;
        this.rsfContext = rsfContext;
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
        //
        this.parameterTypes = targetMethod.getParameterTypes();
        this.parameterObjects = parameterObjects;
        this.optionMap = new HashMap<String, String>();
    }
    public ServiceMetaData getMetaData() {
        return this.metaData;
    }
    public byte getProtocolVersion() {
        return this.rsfContext.getProtocolVersion();
    }
    public String getSerializeType() {
        return this.metaData.getSerializeType();
    }
    public long getRequestID() {
        return this.requestID;
    }
    public String getRemotHost() {
        return this.remoteHost;
    }
    public int getRemotePort() {
        return this.remotePort;
    }
    public String getLocalHost() {
        return this.localHost;
    }
    public int getLocalPort() {
        return this.localPort;
    }
    public int getTimeout() {
        return this.metaData.getClientTimeout();
    }
    public String getMethod() {
        return this.targetMethod.getName();
    }
    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }
    public Object[] getParameterObject() {
        return this.parameterObjects;
    }
    /**获取选项Key集合。*/
    public String[] getOptionKeys() {
        return this.optionMap.keySet().toArray(new String[this.optionMap.size()]);
    }
    /**获取选项数据*/
    public String getOption(String key) {
        return this.optionMap.get(key);
    }
    /**设置选项数据*/
    public void addOption(String key, String value) {
        this.optionMap.put(key, value);
    }
    //
    public ChannelFuture doRequest(Object[]            parameterObjects) {
        //
        this.socketChanne.writeAndFlush(msg);
    }
}
