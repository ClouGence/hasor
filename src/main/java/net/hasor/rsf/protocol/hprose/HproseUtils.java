package net.hasor.rsf.protocol.hprose;
import io.netty.buffer.ByteBuf;
import net.hasor.libs.com.hprose.io.HproseReader;
import net.hasor.libs.com.hprose.io.HproseTags;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.domain.*;
import net.hasor.rsf.utils.ProtocolUtils;
import org.more.bizcommon.json.JSON;
import org.more.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Hprose 工具
 * @version : 2017年1月28日
 * @author 赵永春(zyc@hasor.net)
 */
public class HproseUtils implements HproseConstants {
    public static RequestInfo[] doCall(RsfContext rsfContext, ByteBuf content) throws RsfException {
        // call://<服务ID>/<方法名>?<选项参数> 例：call://[RSF]servicename-version/hello
        HproseReader reader = new HproseReader(content.nioBuffer());
        List<RequestInfo> infoArrays = new ArrayList<RequestInfo>();
        //
        parseRequest(rsfContext, reader, infoArrays);
        content.skipBytes(content.readableBytes());
        //
        return infoArrays.toArray(new RequestInfo[infoArrays.size()]);
    }
    private static void parseRequest(RsfContext rsfContext, HproseReader reader, List<RequestInfo> infoArrays) {
        long requestID = 12345;
        String callName = null;
        try {
            callName = reader.readString();
            reader.reset();
            callName = URLDecoder.decode(callName, "UTF-8");
        } catch (IOException e) {
            throw new RsfException(ProtocolStatus.ProtocolError, "decode callName error -> " + e.getMessage());
        }
        if (!StringUtils.startsWithIgnoreCase(callName, "call://")) {
            throw new RsfException(ProtocolStatus.ProtocolError, "serviceID format error. for example : call://<服务ID>/<方法名>?<选项参数> ,but ->" + callName);
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
            serviceInfo = rsfContext.getServiceInfo(serviceID);
            if (serviceInfo == null) {
                throw new RsfException(ProtocolStatus.NotFound, "serviceID format error. for example : call://<服务ID>/<方法名>?<选项参数> ,but ->" + callName);
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
            lastTag = reader.checkTags(new StringBuilder().append((char) TagList).append((char) TagEnd).append((char) TagCall).toString());
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
            byte[] paramBytes = args[i];
            //Object paramData = (args.length >= i) ? args[i] : null;
            String typeByte = RsfRuntimeUtils.toAsmType(paramType);
            request.addParameter(typeByte, paramBytes, null);
        }
        // .请求参数
        infoArrays.add(request);
        //
        // .如果最后一个读取到的标签是结束标签那么结束整个解析，否则在读取一个标签。
        try {
            if (lastTag == TagEnd)
                return;
            lastTag = reader.checkTags(new StringBuilder().append((char) TagTrue).append((char) TagEnd).append((char) TagCall).toString());
        } catch (Exception e) {
            if (e instanceof RsfException)
                throw (RsfException) e;
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
    public static ByteBuf doResult(long requestID, ResponseInfo response) {
        ByteBuf outBuf = ProtocolUtils.newByteBuf();
        if (response.getStatus() == ProtocolStatus.OK) {
            outBuf.writeByte((byte) 'R');
            outBuf.writeBytes(response.getReturnData());
            outBuf.writeByte((byte) 'z');
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
            outBuf.writeByte((byte) 'E');
            outBuf.writeBytes(data.getBytes());
        }
        return outBuf;
    }
}
