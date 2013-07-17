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
package org.hasor.freemarker.support;
import org.hasor.context.HasorSettingListener;
import org.hasor.context.Settings;
import com.google.inject.Singleton;
/**
 * ≈‰÷√–≈œ¢
 * @version : 2013-4-23
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Singleton
class FreemarkerSettings implements HasorSettingListener {
    private String[] suffix = null;
    public String[] getSuffix() {
        return suffix;
    }
    protected void setSuffix(String[] suffix) {
        this.suffix = suffix;
    }
    @Override
    public void onLoadConfig(Settings newConfig) {
        this.enable = newConfig.getBoolean(FreemarkerConfig_Enable);
        String suffix = newConfig.getString(FreemarkerConfig_Suffix);
        this.contentType = newConfig.getString(FreemarkerConfig_ContentType, "text/html");
        this.suffix = suffix.split(",");
        for (int i = 0; i < this.suffix.length; i++)
            this.suffix[i] = this.suffix[i].trim();
        this.onError = newConfig.getEnum(FreemarkerConfig_OnError, OnErrorMode.class, OnErrorMode.Warning);
    }
}