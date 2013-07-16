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
package org.hasor.test.settings;
import java.io.IOException;
import org.hasor.annotation.SettingsListener;
import org.hasor.context.AppContext;
import org.hasor.context.HasorSettingListener;
import org.hasor.context.Settings;
import org.hasor.test.AbstractTestContext;
import org.junit.Test;
/**
 * 
 * @version : 2013-7-16
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class SettingsTest extends AbstractTestContext {
    @Override
    protected void initContext(AppContext appContext) {
        // TODO Auto-generated method stub
    }
    @Test
    public void test() throws IOException {
        TestSetting sett = this.getAppContext().getInstance(TestSetting.class);
        System.in.read();
    }
}
@SettingsListener
class TestSetting implements HasorSettingListener {
    public String configString = null;
    @Override
    public void onLoadConfig(Settings newConfig) {
        configString = newConfig.getString("PACK");
        System.out.println();
    }
}