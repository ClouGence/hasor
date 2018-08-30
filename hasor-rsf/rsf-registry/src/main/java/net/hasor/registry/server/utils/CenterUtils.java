package net.hasor.registry.server.utils;
import net.hasor.registry.client.domain.ServiceID;
import net.hasor.registry.common.InstanceInfo;
import net.hasor.registry.server.domain.ErrorCode;
import net.hasor.registry.server.domain.ResultDO;
import net.hasor.registry.server.domain.ServiceBean;
import net.hasor.rsf.domain.RsfServiceType;
import net.hasor.utils.CommonCodeUtils;
import net.hasor.utils.ExceptionUtils;

import java.security.NoSuchAlgorithmException;
public class CenterUtils {
    //
    /**/
    public static <T> ResultDO<T> resultOK(T data) {
        ResultDO<T> resultDO = new ResultDO<T>();
        resultDO.setErrorInfo(ErrorCode.OK);
        resultDO.setResult(data);
        resultDO.setSuccess(true);
        return resultDO;
    }
    /**/
    public static <T> ResultDO<T> failedResult(ErrorCode errorCode) {
        ResultDO<T> resultDO = new ResultDO<T>();
        if (errorCode == null) {
            errorCode = ErrorCode.Undefined;
        }
        resultDO.setErrorInfo(errorCode);
        resultDO.setSuccess(false);
        return resultDO;
    }
    //
    //
    public static String getDataKey(ServiceBean serviceInfo) {
        return "/rsf-registry/" + serviceInfo.getGroup() + "/" + serviceInfo.getName() + "/" + serviceInfo.getVersion();
    }
    public static String getDataKey(ServiceID serviceInfo) {
        return "/rsf-registry/" + serviceInfo.getBindGroup() + "/" + serviceInfo.getBindName() + "/" + serviceInfo.getBindVersion();
    }
    public static String getDataKey(InstanceInfo instance, ServiceID serviceID, RsfServiceType serviceType) {
        if (RsfServiceType.Consumer == serviceType) {
            return getDataKey(serviceID) + "/Consumer/" + instance.getInstanceID();
        }
        if (RsfServiceType.Provider == serviceType) {
            return getDataKey(serviceID) + "/Provider/" + instance.getInstanceID();
        }
        return null;
    }
    public static String evalMD5(String dataValue) {
        try {
            return CommonCodeUtils.MD5.getMD5(dataValue);
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}
