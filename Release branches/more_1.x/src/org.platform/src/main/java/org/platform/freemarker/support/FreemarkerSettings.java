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
package org.platform.freemarker.support;
import org.platform.context.SettingListener;
import org.platform.context.Settings;
import com.google.inject.Singleton;
import static org.platform.PlatformConfig.FreemarkerConfig_Enable;
import static org.platform.PlatformConfig.FreemarkerConfig_Suffix;
/**
 * ≈‰÷√–≈œ¢
 * @version : 2013-4-23
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Singleton
public class FreemarkerSettings implements SettingListener {
    private boolean  enable = false;
    private String[] suffix = null;
    //
    public boolean isEnable() {
        return enable;
    }
    protected void setEnable(boolean enable) {
        this.enable = enable;
    }
    public String[] getSuffix() {
        return suffix;
    }
    protected void setSuffix(String[] suffix) {
        this.suffix = suffix;
    }
    @Override
    public void loadConfig(Settings newConfig) {
        this.enable = newConfig.getBoolean(FreemarkerConfig_Enable);
        String suffix = newConfig.getString(FreemarkerConfig_Suffix);
        this.suffix = suffix.split(",");
        for (int i = 0; i < this.suffix.length; i++)
            this.suffix[i] = this.suffix[i].trim();
    }
}