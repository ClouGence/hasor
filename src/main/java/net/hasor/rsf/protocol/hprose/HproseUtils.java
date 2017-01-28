package net.hasor.rsf.protocol.hprose;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.hasor.libs.com.hprose.io.HproseReader;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.domain.RsfException;
import org.more.bizcommon.json.JSON;
import org.more.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
/**
 * Hprose 工具
 * @version : 2017年1月28日
 * @author 赵永春(zyc@hasor.net)
 */
public class HproseUtils {
    public static RequestInfo doCall(RsfContext rsfContext, long requestID, ByteBuf content) throws RsfException {
        // call://<服务ID>/<方法名>?<选项参数>   例：call://[RSF]servicename-version/hello
        byte aByte = content.readByte();
        HproseReader reader = new HproseReader(content.nioBuffer());
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
            serviceInfo = rsfContext.getServiceInfo(serviceID);
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
            request.setClientTimeout(rsfContext.getSettings().getDefaultTimeout());
            request.setReceiveTime(System.currentTimeMillis());
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
            //            int argsCount = reader.readInt(HproseTags.TagOpenbrace);
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
    public static ByteBuf doResult(long requestID, ResponseInfo response) {
        ByteBuf outBuf = ByteBufAllocator.DEFAULT.directBuffer();
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
