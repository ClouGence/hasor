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
package net.demo.hasor.domain.oauth;
import net.demo.hasor.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;
/**
 * OAuth Token 信息
 * @version : 2016年08月11日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AccessInfo {
    private String provider = null;
    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
        this.intType();
    }
    public final String getSource() {
        String provider = this.getProvider();
        return (provider == null ? "NULL" : provider) + ":" + this.getExternalUserID();
    }
    public String toJson() {
        return JsonUtils.toJsonStringSingleLine(this);
    }
    public abstract String getExternalUserID();
    //
    //
    private static final Map<String, Class<? extends AccessInfo>> typeMappingInfo = new HashMap<String, Class<? extends AccessInfo>>();
    private void intType() {
        if (!typeMappingInfo.containsKey(this.getProvider())) {
            typeMappingInfo.put(this.getProvider(), this.getClass());
        }
    }
    public static Class<? extends AccessInfo> getTypeByProvider(String provider) {
        return typeMappingInfo.get(provider);
    }
}