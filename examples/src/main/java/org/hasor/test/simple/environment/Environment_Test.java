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
package org.hasor.test.simple.environment;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.Settings;
import net.hasor.core.SettingsListener;
import net.hasor.core.environment.SimpleEnvironment;
import net.hasor.core.environment.FileEnvironment;
import net.hasor.core.environment.StandardEnvironment;
import org.junit.Test;
/**
 * 环境变量操作演示
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class Environment_Test {
    @Test
    public void testSimpleEnvironment() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testSimpleEnvironment<<--");
        SimpleEnvironment env = new SimpleEnvironment();
        //
        System.out.println(env.getSettings().getString("hasor.forceModule"));
        //HASOR_WORK_HOME
        System.out.println(env.getEnvVar("HASOR_WORK_HOME"));
    }
    @Test
    public void testFileEnvironment() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>testFileEnvironment<<--");
        File settingsFile = new File("src/main/resources/org/hasor/test/simple/environment/full-config.xml");
        FileEnvironment env = new FileEnvironment(settingsFile);
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
        StandardEnvironment env = new StandardEnvironment("org/hasor/test/simple/environment/full-config.xml");
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
    @Test
    public void testStandardEnvironmentVar() throws Exception {
        System.out.println("--->>testStandardEnvironmentVar<<--");
        StandardEnvironment env = new StandardEnvironment("org/hasor/test/simple/environment/full-config.xml");
        //
        //JAVA_HOME
        System.out.println(env.getEnvVar("JAVA_HOME"));
        //HASOR_WORK_HOME
        System.out.println(env.getEnvVar("HASOR_WORK_HOME"));
        //javac.exe
        System.out.println(env.evalString("%JAVA_HOME%/bin/javac.exe"));
        //notepad
        System.out.println(env.evalEnvVar("notepad"));
    }
}
class TestSetting implements SettingsListener {
    public void onLoadConfig(Settings newConfig) {
        System.out.println(newConfig.getString("hasor.forceModule"));
    }
}