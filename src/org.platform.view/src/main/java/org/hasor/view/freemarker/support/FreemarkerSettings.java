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
package org.hasor.view.freemarker.support;
import static org.hasor.view.freemarker.ConfigurationFactory.FreemarkerConfig_ContentType;
import static org.hasor.view.freemarker.ConfigurationFactory.FreemarkerConfig_Enable;
import static org.hasor.view.freemarker.ConfigurationFactory.FreemarkerConfig_OnError;
import static org.hasor.view.freemarker.ConfigurationFactory.FreemarkerConfig_Suffix;
import org.hasor.setting.SettingListener;
import org.hasor.setting.Settings;
import com.google.inject.Singleton;
/**
 * 配置信息
 * @version : 2013-4-23
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
class FreemarkerSettings implements SettingListener {
    public static enum OnErrorMode {
        /**抛出异常*/
        ThrowError,
        /**打印到控制台或日志*/
        PrintOnConsole,
        /**忽略，仅仅产生一条警告消息*/
        Warning,
        /**打印到页面*/
        PrintOnPage
    };
    private boolean     enable      = false;
    private String      contentType = null;
    private String[]    suffix      = null;
    private OnErrorMode onError     = null;
    //
    public boolean isEnable() {
        return enable;
    }
    public String[] getSuffix() {
        return suffix;
    }
    public OnErrorMode getOnError() {
        return onError;
    }
    public String getContentType() {
        return contentType;
    }
    protected void setContentType(String contentType) {
        this.contentType = contentType;
    }
    protected void setEnable(boolean enable) {
        this.enable = enable;
    }
    protected void setSuffix(String[] suffix) {
        this.suffix = suffix;
    }
    protected void setOnError(OnErrorMode onError) {
        this.onError = onError;
    }
    @Override
    public void loadConfig(Settings newConfig) {
        this.enable = newConfig.getBoolean(FreemarkerConfig_Enable);
        String suffix = newConfig.getString(FreemarkerConfig_Suffix);
        this.contentType = newConfig.getString(FreemarkerConfig_ContentType, "text/html");
        this.suffix = suffix.split(",");
        for (int i = 0; i < this.suffix.length; i++)
            this.suffix[i] = this.suffix[i].trim();
        this.onError = newConfig.getEnum(FreemarkerConfig_OnError, OnErrorMode.class, OnErrorMode.Warning);
    }
}