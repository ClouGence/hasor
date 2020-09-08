package net.hasor.dataway.domain;
import java.util.Map;

public class ApiInfoData {
    private String              apiId;          // Api ID
    private String              method;         // 请求方法
    private String              apiPath;        // 请求路径
    private ApiTypeEnum         type;           // 脚本类型 DataQL or SQL
    private ApiStatusEnum       status;         // 接口状态
    private String              comment;        // 注释
    private ApiTypeData         requestInfo;    // 请求参数信息
    private ApiTypeData         responseInfo;   // 响应结果信息
    private Map<String, Object> optionMap;      // 接口选项
    private Map<String, Object> prepareHint;    // 预定义 Hint

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public ApiTypeEnum getType() {
        return type;
    }

    public void setType(ApiTypeEnum type) {
        this.type = type;
    }

    public ApiStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ApiStatusEnum status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ApiTypeData getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(ApiTypeData requestInfo) {
        this.requestInfo = requestInfo;
    }

    public ApiTypeData getResponseInfo() {
        return responseInfo;
    }

    public void setResponseInfo(ApiTypeData responseInfo) {
        this.responseInfo = responseInfo;
    }

    public Map<String, Object> getOptionMap() {
        return optionMap;
    }

    public void setOptionMap(Map<String, Object> optionMap) {
        this.optionMap = optionMap;
    }

    public Map<String, Object> getPrepareHint() {
        return prepareHint;
    }

    public void setPrepareHint(Map<String, Object> prepareHint) {
        this.prepareHint = prepareHint;
    }
}