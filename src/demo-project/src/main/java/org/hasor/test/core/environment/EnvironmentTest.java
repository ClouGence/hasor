/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.hasor.test.core.environment;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import net.hasor.core.Settings;
import net.hasor.core.SettingsListener;
import net.hasor.core.environment.DefaultEnvironment;
import net.hasor.core.environment.StandardEnvironment;
import org.junit.Test;
import org.more.util.ResourcesUtils;
/**
 * 环境变量操作演示
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class EnvironmentTest {
    @Test
    public void testDefaultEnvironment() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testDefaultEnvironment<<--");
        URL settingsURI = ResourcesUtils.getResource("/org/hasor/test/core/settings/full-config.xml");
        DefaultEnvironment env = new DefaultEnvironment(settingsURI.toURI());
        //
        System.out.println(env.getSettings().getString("hasor.forceModule"));
        //
        env.addSettingsListener(new TestSetting());
        //
        Thread.sleep(10000);
    }
    @Test
    public void testStandardEnvironment() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testStandardEnvironment<<--");
        StandardEnvironment env = new StandardEnvironment();
        //
        System.out.println(env.getSettings().getString("hasor.forceModule"));
        //
        env.addSettingsListener(new TestSetting());
        //
        Thread.sleep(10000);
    }
    //    
    //    protected void initContext(AppContext appContext) {
    //        //
    //        appContext.getEnvironment().addEnvVar("notepad", "%windir%/notepad.exe");
    //    }
    //    @Test
    //    public void test() throws Exception {
    //        System.out.println();
    //        System.out.println();
    //        System.out.println();
    //        Environment env = this.getAppContext().getEnvironment();
    //        //JAVA_HOME
    //        System.out.println(env.getEnvVar("JAVA_HOME"));
    //        //HASOR_WORK_HOME
    //        System.out.println(env.getEnvVar("HASOR_WORK_HOME"));
    //        //javac.exe
    //        System.out.println(env.evalString("%JAVA_HOME%/bin/javac.exe"));
    //        //notepad
    //        System.out.println(env.evalEnvVar("notepad"));
    //    }
}
class TestSetting implements SettingsListener {
    public void onLoadConfig(Settings newConfig) {
        System.out.println(newConfig.getString("hasor.forceModule"));
    }
}