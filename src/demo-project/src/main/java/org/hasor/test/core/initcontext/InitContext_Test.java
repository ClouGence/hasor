/*
 * Copyright 2008-2009 the original ÕÔÓÀ´º(zyc@hasor.net).
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
package org.hasor.test.core.initcontext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import net.hasor.core.Settings;
import net.hasor.core.SettingsListener;
import net.hasor.core.context.init.DefaultInitContext;
import net.hasor.core.context.init.StandardInitContext;
import org.junit.Test;
import org.more.util.ResourcesUtils;
/**
 * 
 * @version : 2013-7-16
 * @author ÕÔÓÀ´º (zyc@hasor.net)
 */
public class InitContext_Test {
    @Test
    public void testDefaultInitContext() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testDefaultInitContext<<--");
        URL settingsURI = ResourcesUtils.getResource("/org/hasor/test/core/settings/full-config.xml");
        DefaultInitContext initContext = new DefaultInitContext(settingsURI.toURI());
        //
        System.out.println(initContext.getSettings().getString("hasor.forceModule"));
        //
        initContext.addSettingsListener(new TestSetting());
        //
        Thread.sleep(10000);
    }
    @Test
    public void testStandardInitContext() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testStandardInitContext<<--");
        StandardInitContext initContext = new StandardInitContext();
        //
        System.out.println(initContext.getSettings().getString("hasor.forceModule"));
        //
        initContext.addSettingsListener(new TestSetting());
        //
        Thread.sleep(10000);
    }
}
class TestSetting implements SettingsListener {
    public String configString = null;
    public void onLoadConfig(Settings newConfig) {
        configString = newConfig.getString("hasor.forceModule");
        System.out.println("onLoadConfig£º" + configString);
    }
}