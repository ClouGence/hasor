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
package net.hasor.rsf.protocol.hprose;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import net.hasor.libs.com.hprose.io.HproseReader;
import net.hasor.libs.com.hprose.io.HproseTags;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.protocol.rsf.v1.RequestBlock;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
/**
 * RSF 解码器
 * @version : 2017年1月26日
 * @author 赵永春(zyc@hasor.net)
 */
public class HproseDecoder extends LengthFieldBasedFrameDecoder {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private RsfContext  rsfContext;
    private RsfSettings rsfSettings;
    //
    public HproseDecoder(RsfContext rsfContext) {
        // lengthFieldOffset   = 2
        // lengthFieldLength   = 3
        // lengthAdjustment    = 0
        // initialBytesToStrip = 0
        super(RequestBlock.DataMaxSize, 0, 0, 0, 0);
        this.rsfContext = rsfContext;
        this.rsfSettings = rsfContext.getSettings();
    }
    //
    /** 解码 */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 0x41('A'): RPC 参数
        // 0x7A('z'): 结束
        //
        byte[] data1 = new byte[4];
        byte[] data2 = new byte[4];
        in.readBytes(data1);
        boolean halfDuplex = false;
        if (data1[0] == (data1[0] | 0x00)) {
            // - 半双工
            halfDuplex = true;
        } else if (data1[0] == (data1[0] | 0x80)) {
            // - 全双工
            halfDuplex = false;
            in.readBytes(data2);
        }
        //
        /* ------------------------------------------------ */
        char aChar = (char) in.readByte();
        HproseReader reader = new HproseReader(in.nioBuffer());
        long requestID = 111;
        try {
            if (aChar == 'F') {
                //                             0x46('F'): 函数列表
            } else if (aChar == 'C') {
                //                             0x43('C'): RPC 调用
                RequestInfo requestInfo = doCall(requestID, reader);
                ctx.pipeline().writeAndFlush(requestInfo);
            } else if (aChar == 'R') {
                //                             0x52('R'): RPC 结果
            } else if (aChar == 'E') {
                //                             0x45('E'): RPC 错误
            }
        } catch (RsfException e) {
            //                                 xxx
        }
        /* ------------------------------------------------ */
        return null;
    }
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }
    private RequestInfo doCall(long requestID, HproseReader reader) throws RsfException {
        // call://<服务ID>/<方法名>?<选项参数>   例：call://[RSF]servicename-version/hello
        String callName = null;
        try {
            callName = reader.readString();
            callName = URLDecoder.decode(callName, "UTF-8");
        } catch (IOException e) {
            throw new RsfException(ProtocolStatus.ProtocolError, "decode callName error -> " + e.getMessage());
        }
        if (!StringUtils.startsWithIgnoreCase(callName, "call://")) {
            throw new RsfException(ProtocolStatus.ProtocolError, "serviceID format error. for example : call://<服务ID>/<方法名>?<选项参数>");
        }
        // 创建 RequestInfo 对象
        RsfBindInfo<?> serviceInfo = null;
        RequestInfo request = new RequestInfo();
        request.setRequestID(requestID);
        try {
            //call://<服务ID>/<方法名>?<选项参数>
            callName = callName.substring("call://".length());
            String[] callNameSplit = callName.split("/");
            String[] lastParams = callNameSplit[1].split("\\?");
            //
            String serviceID = callNameSplit[0];
            String methodName = lastParams[0];
            String options = lastParams.length == 2 ? lastParams[1] : null;
            serviceInfo = this.rsfContext.getServiceInfo(serviceID);
            if (serviceInfo == null) {
                throw new RsfException(ProtocolStatus.NotFound, "serviceID format error. for example : call://<服务ID>/<方法名>?<选项参数>");
            }
            //
            request.setServiceGroup(serviceInfo.getBindGroup());
            request.setServiceName(serviceInfo.getBindName());
            request.setServiceVersion(serviceInfo.getBindVersion());
            request.setTargetMethod(methodName);
            request.setMessage(false);
            request.setSerializeType("Hprose");
            request.setClientTimeout(this.rsfSettings.getDefaultTimeout());
        } catch (Exception e) {
            if (e instanceof RsfException)
                throw (RsfException) e;
            throw new RsfException(ProtocolStatus.Unknown, "error(" + e.getClass() + ") -> " + e.getMessage());
        }
        // 确定方法
        Method atMethod = null;
        Object[] args = null;
        try {
            String methodName = request.getTargetMethod();
            int argsCount = reader.readInt(HproseTags.TagOpenbrace);
            args = reader.readObjectArray();
            args = (args == null) ? new Object[0] : args;
            Method[] allMethods = serviceInfo.getBindType().getMethods();
            for (Method method : allMethods) {
                if (!method.getName().equals(methodName))
                    continue;
                if (args.length != method.getParameterTypes().length)
                    continue;
                atMethod = method;
                break;
            }
            if (atMethod == null) {
                throw new RsfException(ProtocolStatus.NotFound, "serviceID : " + serviceInfo.getBindID() + " ,not found method " + methodName);
            }
        } catch (Exception e) {
            if (e instanceof RsfException)
                throw (RsfException) e;
            throw new RsfException(ProtocolStatus.Unknown, "error(" + e.getClass() + ") -> " + e.getMessage());
        }
        //
        for (Class<?> paramType : atMethod.getParameterTypes()) {
            byte[] paramData = null;
            request.addParameter(paramType.getName(), paramData, null);
        }
        //
        return request;
    }
    //
}