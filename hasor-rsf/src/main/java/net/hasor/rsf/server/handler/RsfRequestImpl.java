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
package net.hasor.rsf.server.handler;
import io.netty.channel.ChannelHandlerContext;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.hasor.rsf.context.RsfContext;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.protocol.message.RequestMsg;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.server.RsfRequest;
import org.more.util.StringUtils;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfRequestImpl implements RsfRequest {
    private ServiceMetaData       metaData         = null;
    private RequestMsg            requestMessage   = null;
    private ChannelHandlerContext channelContext   = null;
    private RsfContext            rsfContext       = null;
    //
    private String                remoteHost       = null;
    private int                   remotePort       = 0;
    private String                localHost        = null;
    private int                   localPort        = 0;
    //
    private Class<?>[]            parameterTypes   = null;
    private Object[]              parameterObjects = null;
    //
    //
    public RsfRequestImpl(RequestMsg requestMessage, ChannelHandlerContext channelContext, RsfContext rsfContext) {
        this.metaData = rsfContext.getService(requestMessage.getServiceName());
        this.requestMessage = requestMessage;
        this.channelContext = channelContext;
        this.rsfContext = rsfContext;
        //remote
        SocketAddress rAddress = channelContext.channel().remoteAddress();//InetSocketAddress
        if (rAddress instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) rAddress;
            this.remoteHost = address.getAddress().getHostAddress();
            this.remotePort = address.getPort();
        }
        //local
        SocketAddress lAddress = channelContext.channel().localAddress();//InetSocketAddress
        if (lAddress instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) lAddress;
            this.localHost = address.getAddress().getHostAddress();
            this.localPort = address.getPort();
        }
        //
    }
    public ServiceMetaData getMetaData() {
        return this.metaData;
    }
    public byte getProtocolVersion() {
        return this.requestMessage.getVersion();
    }
    public String getSerializeType() {
        return this.requestMessage.getSerializeType();
    }
    public long getRequestID() {
        return this.requestMessage.getRequestID();
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
        return this.requestMessage.getClientTimeout();
    }
    public long getReceiveTime() {
        return this.requestMessage.getReceiveTime();
    }
    public String getMethod() {
        return this.requestMessage.getTargetMethod();
    }
    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }
    public Object[] getParameterObject() {
        return this.parameterObjects;
    }
    public String[] getOptionKeys() {
        return this.requestMessage.getOptionKeys();
    }
    public String getOption(String key) {
        return this.requestMessage.getOption(key);
    }
    public void addOption(String key, String value) {
        this.requestMessage.addOption(key, value);
    }
    //
    //
    //
    public void init() throws Throwable {
        //
        SerializeFactory serializeFactory = this.rsfContext.getSerializeFactory();
        this.parameterObjects = this.requestMessage.toParameters(serializeFactory);
        //
        String[] pTypes = this.requestMessage.getParameterTypes();
        this.parameterTypes = new Class<?>[pTypes.length];
        for (int i = 0; i < pTypes.length; i++) {
            this.parameterTypes[i] = toJavaType(pTypes[i], Thread.currentThread().getContextClassLoader());
        }
    }
    /**使用指定的ClassLoader将一个asm类型转化为Class对象。*/
    private static Class<?> toJavaType(final String tType, final ClassLoader loader) throws ClassNotFoundException {
        if (/*   */tType.equals("I") == true || StringUtils.equalsIgnoreCase(tType, "int") == true) {
            return int.class;
        } else if (tType.equals("B") == true || StringUtils.equalsIgnoreCase(tType, "byte") == true) {
            return byte.class;
        } else if (tType.equals("C") == true || StringUtils.equalsIgnoreCase(tType, "char") == true) {
            return char.class;
        } else if (tType.equals("D") == true || StringUtils.equalsIgnoreCase(tType, "double") == true) {
            return double.class;
        } else if (tType.equals("F") == true || StringUtils.equalsIgnoreCase(tType, "float") == true) {
            return float.class;
        } else if (tType.equals("J") == true || StringUtils.equalsIgnoreCase(tType, "long") == true) {
            return long.class;
        } else if (tType.equals("S") == true || StringUtils.equalsIgnoreCase(tType, "short") == true) {
            return short.class;
        } else if (tType.equals("Z") == true || StringUtils.equalsIgnoreCase(tType, "bit") == true || StringUtils.equalsIgnoreCase(tType, "boolean") == true) {
            return boolean.class;
        } else if (tType.equals("V") == true || StringUtils.equalsIgnoreCase(tType, "void") == true) {
            return void.class;
        } else if (tType.charAt(0) == '[') {
            int length = 0;
            while (true) {
                if (tType.charAt(length) != '[') {
                    break;
                }
                length++;
            }
            String arrayType = tType.substring(length, tType.length());
            Class<?> returnType = toJavaType(arrayType, loader);
            for (int i = 0; i < length; i++) {
                Object obj = Array.newInstance(returnType, length);
                returnType = obj.getClass();
            }
            return returnType;
        } else {
            return loader.loadClass(tType);
        }
    }
}
