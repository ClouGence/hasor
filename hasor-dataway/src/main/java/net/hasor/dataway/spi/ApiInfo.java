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
package net.hasor.dataway.spi;
import java.util.Map;

/**
 * 封装 API 信息
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-19
 */
public class ApiInfo {
    private boolean             perform;        // 本次调用是否为perform
    private String              apiID;          // API ID
    private String              releaseID;      // 发布的版本ID
    private String              method;         // Http Method
    private String              apiPath;        // Path
    private Map<String, Object> parameterMap;   // 请求参数
    private Map<String, Object> optionMap;      // 选项参数

    public boolean isPerform() {
        return perform;
    }

    public void setPerform(boolean perform) {
        this.perform = perform;
    }

    public String getApiID() {
        return apiID;
    }

    public void setApiID(String apiID) {
        this.apiID = apiID;
    }

    public String getReleaseID() {
        return releaseID;
    }

    public void setReleaseID(String releaseID) {
        this.releaseID = releaseID;
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

    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, Object> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public Map<String, Object> getOptionMap() {
        return this.optionMap;
    }

    public void setOptionMap(Map<String, Object> optionMap) {
        this.optionMap = optionMap;
    }
}