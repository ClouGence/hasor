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
package org.hasor.servlet.encoding;
import org.hasor.setting.SettingListener;
import org.hasor.setting.Settings;
/**
 * 
 * @version : 2013-6-8
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class EncodingSettings implements SettingListener {
    private String requestEncoding  = null;
    private String responseEncoding = null;
    //
    public String getRequestEncoding() {
        return requestEncoding;
    }
    public void setRequestEncoding(String requestEncoding) {
        this.requestEncoding = requestEncoding;
    }
    public String getResponseEncoding() {
        return responseEncoding;
    }
    public void setResponseEncoding(String responseEncoding) {
        this.responseEncoding = responseEncoding;
    }
    @Override
    public void loadConfig(Settings newConfig) {
        this.requestEncoding = newConfig.getString("httpServlet.requestEncoding.requestEncoding", "utf-8");
        this.responseEncoding = newConfig.getString("httpServlet.requestEncoding.responseEncoding", "utf-8");
    }
}