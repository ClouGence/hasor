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
package org.hasor.test.simple.settings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import net.hasor.core.setting.FileSettings;
import net.hasor.core.setting.InputStreamSettings;
import net.hasor.core.setting.StandardContextSettings;
import org.junit.Test;
import org.more.util.ResourcesUtils;
/**
 * 
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class Settings_Test {
    @Test
    public void testStream() throws IOException {
        System.out.println("--->>testStream<<--");
        InputStream inStream = ResourcesUtils.getResourceAsStream("/org/hasor/test/simple/settings/full-config.xml");
        InputStreamSettings settings = new InputStreamSettings(inStream);
        //
        System.out.println(settings.getString("hasor-mvc.httpServlet.errorCaseCount"));
    }
    @Test
    public void testFileSettings_1() throws IOException {
        System.out.println("--->>testFileSettings_1<<--");
        File inFile = new File("src/main/resources/org/hasor/test/simple/settings/test-settings-a-config.xml");
        FileSettings settings = new FileSettings(inFile);
        //
        System.out.println(settings.getString("custom.wwwSource.url"));
        System.out.println(settings.getString("custom.dbSource.url"));//不存在这个配置内容
        System.out.println(settings.getString("custom.id"));
    }
    @Test
    public void testFileSettings_2() throws IOException {
        System.out.println("--->>testFileSettings_2<<--");
        File inFile1 = new File("src/main/resources/org/hasor/test/simple/settings/test-settings-a-config.xml");
        File inFile2 = new File("src/main/resources/org/hasor/test/simple/settings/test-settings-b-config.xml");
        FileSettings settings = new FileSettings();
        settings.addFile(inFile1);
        settings.addFile(inFile2);
        settings.refresh();
        //
        System.out.println(settings.getString("custom.wwwSource.url"));
        System.out.println(settings.getString("custom.dbSource.url"));
        System.out.println(settings.getString("custom.id"));
        settings.refresh();//TODO 在这里中断程序，修改配置文件然后继续执行。
        System.out.println(settings.getString("custom.wwwSource.url"));
        System.out.println(settings.getString("custom.dbSource.url"));
        System.out.println(settings.getString("custom.id"));
        //
    }
    @Test
    public void testStandardContextSettings() throws IOException {
        System.out.println("--->>testStandardContextSettings<<--");
        StandardContextSettings settings = new StandardContextSettings("hasor-config.xml");
        //
        System.out.println(Hasor.logString(settings.getStringArray("hasor.loadPackages")));
        System.out.println(Hasor.logString(settings.getStringArray("environmentVar.HASOR_WORK_HOME")));//不存在这个配置内容
    }
    @Test
    public void testNSSettings() throws IOException {
        System.out.println("--->>testNSSettings<<--");
        File inFile = new File("src/main/resources/org/hasor/test/simple/settings/ns-config.xml");
        FileSettings settings = new FileSettings(inFile);
        //
        //Mode1
        Settings mod1 = settings.getSetting("http://mode1.myProject.net");
        Settings mod2 = settings.getSetting("http://mode2.myProject.net");
        System.out.println(mod1.getString("appSettings.serverLocal.url"));
        System.out.println(mod2.getString("appSettings.serverLocal.url"));
    }
    @Test
    public void testNS2Settings() throws IOException {
        System.out.println("--->>testNS2Settings<<--");
        File inFile1 = new File("src/main/resources/org/hasor/test/simple/settings/ns1-config.xml");
        File inFile2 = new File("src/main/resources/org/hasor/test/simple/settings/ns2-config.xml");
        FileSettings settings = new FileSettings();
        settings.addFile(inFile1);
        settings.addFile(inFile2);
        settings.refresh();
        //
        //Mode1
        Settings mod1 = settings.getSetting("http://mode1.myProject.net");
        Settings mod2 = settings.getSetting("http://mode2.myProject.net");
        System.out.println(mod1.getString("appSettings.serverLocal.url"));
        System.out.println(mod2.getString("appSettings.serverLocal.url"));
    }
    //    @Test
    //    public void testMappingInitContextSettings() throws IOException {
    //        System.out.println("--->>testMappingInitContextSettings<<--");
    //        MappingContextSettings settings = new MappingContextSettings();
    //        //
    //        System.out.println(Hasor.logString(settings.getString("work")));//不存在这个配置内容
    //    }
}