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
package net.hasor.rsf.protocol.http_hprose;
import hprose.io.HproseReader;
import hprose.io.HproseTags;
import hprose.io.HproseWriter;
import io.netty.buffer.ByteBuf;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.domain.*;
import net.hasor.rsf.utils.ProtocolUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.json.JSON;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.*;

import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import static io.netty.handler.codec.http.HttpHeaders.Names.ORIGIN;

/**
 * Hprose 工具
 * @version : 2017年1月28日
 * @author 赵永春 (zyc@hasor.net)
 */
public class HproseUtils implements HproseConstants {
    /***/
    public static String[] doFunction(RsfContext rsfContext) throws IOException {
        Set<String> allMethod = new LinkedHashSet<String>();
        allMethod.add("*");
        //
        // .请求函数列表
        List<String> serviceIDs = rsfContext.getServiceIDs();
        for (String serviceID : serviceIDs) {
            RsfBindInfo<?> serviceInfo = rsfContext.getServiceInfo(serviceID);
            if (serviceInfo.isShadow() || RsfServiceType.Provider != serviceInfo.getServiceType())
                continue;
            String aliasName = serviceInfo.getAliasName(HPROSE);
            if (StringUtils.isBlank(aliasName)) {
                continue;
            }
            //
            Method[] methodArrays = serviceInfo.getBindType().getMethods();
            for (Method method : methodArrays) {
                StringBuilder define = new StringBuilder();
                define = define.append(aliasName);
                define = define.append("_");
                define = define.append(method.getName());
                allMethod.add(define.toString());
            }
            //
        }
        return allMethod.toArray(new String[allMethod.size()]);
    }

    /***/
    public static RequestInfo[] doCall(RsfContext rsfContext, InputStream content, String requestURI, String origin) throws RsfException, IOException {
        //
        HproseReader reader = new HproseReader(content);
        List<RequestInfo> infoArrays = new ArrayList<RequestInfo>();
        //
        parseRequest(rsfContext, reader, infoArrays);
        content.skip(content.available());
        //
        for (RequestInfo info : infoArrays) {
            info.addOption(LOCATION, requestURI);
            info.addOption(ORIGIN, origin);
        }
        //
        return infoArrays.toArray(new RequestInfo[infoArrays.size()]);
    }
    //
    //

    /***/
    private static void parseRequest(RsfContext rsfContext, HproseReader reader, List<RequestInfo> infoArrays) throws IOException {
        long requestID = 12345;
        String callName = null;
        try {
            callName = reader.readString();
            reader.reset();
        } catch (IOException e) {
            throw new RsfException(ProtocolStatus.ProtocolError, "decode callName error -> " + e.getMessage());
        }
        //
        // 创建 RequestInfo 对象
        RsfBindInfo<?> serviceInfo = null;
        RequestInfo request = new RequestInfo();
        request.setRequestID(requestID);
        try {
            String[] lastParams = callName.split("_");
            String methodName = lastParams[lastParams.length - 1];
            String serviceID = callName.substring(0, callName.length() - methodName.length() - 1);
            //
            serviceInfo = rsfContext.getServiceInfo(HPROSE, serviceID);
            if (serviceInfo == null) {
                throw new RsfException(ProtocolStatus.NotFound, "serviceID not found in alias. -> " + serviceID);
            }
            //
            request.setServiceGroup(serviceInfo.getBindGroup());
            request.setServiceName(serviceInfo.getBindName());
            request.setServiceVersion(serviceInfo.getBindVersion());
            request.setTargetMethod(methodName);
            request.setMessage(false);
            request.setSerializeType("Hprose");
            request.setClientTimeout(rsfContext.getSettings().getDefaultTimeout());
            request.setReceiveTime(System.currentTimeMillis());
        } catch (Exception e) {
            if (e instanceof RsfException)
                throw (RsfException) e;
            throw new RsfException(ProtocolStatus.Unknown, "error(" + e.getClass() + ") -> " + e.getMessage());
        }
        // 确定方法
        int lastTag = 0;
        Method atMethod = null;
        Class<?>[] parameterTypes = null;
        byte[][] args = null;
        try {
            int argCount = 0;
            String methodName = request.getTargetMethod();
            lastTag = reader.checkTags(String.valueOf((char) TagList) + (char) TagEnd + (char) TagCall);
            if (lastTag == HproseTags.TagList) {
                reader.reset();
                argCount = reader.readInt(HproseTags.TagOpenbrace);
                args = new byte[argCount][];
                for (int i = 0; i < argCount; i++) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    reader.readRaw(out);
                    args[i] = out.toByteArray();
                }
                reader.readInt(HproseTags.TagClosebrace);
            }
            args = (args == null) ? new byte[0][] : args;
            Method[] allMethods = serviceInfo.getBindType().getMethods();
            for (Method method : allMethods) {
                if (!method.getName().equals(methodName))
                    continue;
                parameterTypes = method.getParameterTypes();
                if (argCount != parameterTypes.length)
                    continue;
                atMethod = method;
                break;
            }
            if (atMethod == null) {
                throw new RsfException(ProtocolStatus.NotFound, "serviceID : " + serviceInfo.getBindID() + " ,not found method " + methodName);
            }
            //
        } catch (Exception e) {
            if (e instanceof RsfException)
                throw (RsfException) e;
            throw new RsfException(ProtocolStatus.Unknown, "error(" + e.getClass() + ") -> " + e.getMessage());
        }
        // .参数处理(isRef是否为引用参数调用 (遇到引用参数方法，会在response时将请求参数一同返回给客户端)
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramType = parameterTypes[i];
            HproseReader paramDataReader = new HproseReader(args[i]);
            Object paramData = paramDataReader.unserialize(paramType);
            request.addParameter(paramType.getName(), paramData);
        }
        // .请求参数
        infoArrays.add(request);
        //
        // .如果最后一个读取到的标签是结束标签那么结束整个解析，否则在读取一个标签。
        try {
            if (lastTag == TagEnd)
                return;
            lastTag = reader.checkTags(String.valueOf((char) TagTrue) + (char) TagEnd + (char) TagCall);
        } catch (Exception e) {
            if (e instanceof RsfException) {
                throw (RsfException) e;
            }
            throw new RsfException(ProtocolStatus.SerializeError, "error(" + e.getClass() + ") reader.checkTags -> " + e.getMessage());
        }
        //
        // .当读取的最后一个标签不是结束标签那么继续处理直到遇到结束标签
        if (lastTag == TagEnd) {
            return;
        }
        // .如果下一个标签还是一个call，表示当前请求是批量调用。
        if (lastTag == TagCall) {
            throw new RsfException(ProtocolStatus.ProtocolError, "hprose batch calls, is not support.");
            //parseRequest(rsfContext, reader, infoArrays);
        }
        // .表示是参数引用调用，面对参数引用时候在响应时需要讲参数一同响应给客户端
        if (lastTag == TagTrue) {
            throw new RsfException(ProtocolStatus.ProtocolError, "hprose ref param, is not support.");
        }
    }

    /***/
    public static void parseResponse(long requestID, ResponseInfo response, OutputStream output) throws IOException {
        if (response.getStatus() == ProtocolStatus.OK) {
            output.write(new byte[] { 'R' });
            ByteArrayOutputStream binary = new ByteArrayOutputStream();
            HproseWriter writer = new HproseWriter(binary);
            writer.serialize(response.getReturnData());
            byte[] encode = binary.toByteArray();
            output.write(encode);
            output.write(new byte[] { 'z' });
            //
        } else {
            Map<String, String> errorMsg = new HashMap<String, String>();
            String[] optionKeys = response.getOptionKeys();
            if (optionKeys != null) {
                for (String optKey : optionKeys) {
                    errorMsg.put(optKey, response.getOption(optKey));
                }
            }
            errorMsg.put("requestID", String.valueOf(requestID));
            errorMsg.put("status", String.valueOf(response.getStatus()));
            String jsonData = JSON.toString(errorMsg);
            String data = "s" + jsonData.length() + "\"" + jsonData + "\"z";
            //
            output.write(new byte[] { 'E' });
            output.write(data.getBytes());
        }
    }

    /***/
    public static ByteBuf encodeRequest(RsfContext rsfContext, RequestInfo request) throws IOException {
        RsfBindInfo<?> bindInfo = rsfContext.getServiceInfo(request.getServiceGroup(), request.getServiceName(), request.getServiceVersion());
        String aliasName = bindInfo.getAliasName(HPROSE);
        //
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HproseWriter writer = new HproseWriter(out);
        writer.writeString(aliasName + "_" + request.getTargetMethod());
        //
        writer.writeArray(request.getParameterValues().toArray());
        //
        ByteBuf outBuf = ProtocolUtils.newByteBuf();
        outBuf.writeByte('C');
        outBuf.writeBytes(out.toByteArray());
        outBuf.writeByte('z');
        return outBuf;
    }

    public static Object decodeResponse(InputStream inputStream) throws IOException {
        int aByte = inputStream.read();
        if ((char) aByte == 'R') {
            HproseReader reader = new HproseReader(inputStream);
            return reader.unserialize();
        }
        return null;
    }
}