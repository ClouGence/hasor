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
package net.test.simple._04_settings;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Date;
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import net.hasor.core.setting.InputStreamSettings;
import org.junit.Test;
import org.more.util.ResourcesUtils;
/**
 * Settings 接口功能测试。
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class Simple_Test {
    /*获取 Xml 中配置信息测试*/
    @Test
    public void test_Simple() throws IOException, URISyntaxException {
        System.out.println("--->>test_Simple<<--");
        InputStream inStream = ResourcesUtils.getResourceAsStream("net/test/simple/_04_settings/simple-config.xml");
        InputStreamSettings settings = new InputStreamSettings(inStream);
        settings.loadSettings();//装载配置文件
        //
        String myName = settings.getString("mySelf.myName");
        Hasor.logInfo("my Name is %s.", myName);
        //
        int myAge = settings.getInteger("mySelf.myAge");
        Hasor.logInfo("my Age is %s.", myAge);
        //
        Date myBirthday = settings.getDate("mySelf.myBirthday");
        Hasor.logInfo("my Birthday is %s.", myBirthday);//TODO 需要解决通用格式转换问题
        //
        String myWork = settings.getString("mySelf.myWork");
        Hasor.logInfo("my Work is %s.", myWork);
        //
        String myProjectURL = settings.getString("mySelf.myProjectURL");
        Hasor.logInfo("my Project is %s.", myProjectURL);
    }
    /*测试 Xml 配置中存在多个命名空间情况下，分别获取不同命名空间的相同配置（在同一个Xml 文件）。*/
    @Test
    public void test_all_in_one() throws IOException {
        System.out.println("--->>test_all_in_one<<--");
        InputStream inStream = ResourcesUtils.getResourceAsStream("net/test/simple/_04_settings/ns-all-in-one-config.xml");
        InputStreamSettings settings = new InputStreamSettings(inStream);
        settings.loadSettings();//装载配置文件
        //
        Settings ns1_settings = settings.getSettings("http://mode1.myProject.net");
        Settings ns2_settings = settings.getSettings("http://mode2.myProject.net");
        //
        String ns1_local = ns1_settings.getString("appSettings.serverLocal.url");
        String ns2_local = ns2_settings.getString("appSettings.serverLocal.url");
        String[] all_local = settings.getStringArray("appSettings.serverLocal.url");
        //
        Hasor.logInfo("ns1 is %s.", ns1_local);
        Hasor.logInfo("ns2 is %s.", ns2_local);
        Hasor.logInfo("ns is %s.", (Object) all_local);//同时取得全部命名空间下的相同配置节点配置信息。
    }
    /*测试 Xml 配置中存在多个命名空间情况下，分别获取不同命名空间的相同配置（在不同 Xml 文件）。*/
    @Test
    public void test_mergeNS() throws IOException {
        System.out.println("--->>test_mergeNS<<--");
        InputStream ns1_inStream = ResourcesUtils.getResourceAsStream("net/test/simple/_04_settings/ns1-config.xml");
        InputStream ns2_inStream = ResourcesUtils.getResourceAsStream("net/test/simple/_04_settings/ns2-config.xml");
        InputStreamSettings settings = new InputStreamSettings(new InputStream[] { ns1_inStream, ns2_inStream });
        settings.loadSettings();//装载配置文件
        //
        Settings ns1_settings = settings.getSettings("http://mode1.myProject.net");
        Settings ns2_settings = settings.getSettings("http://mode2.myProject.net");
        //
        String ns1_local = ns1_settings.getString("appSettings.serverLocal.url");
        String ns2_local = ns2_settings.getString("appSettings.serverLocal.url");
        String[] all_local = settings.getStringArray("appSettings.serverLocal.url");//同时取得全部命名空间下的相同配置节点配置信息。
        //
        Hasor.logInfo("ns1 is %s.", ns1_local);
        Hasor.logInfo("ns2 is %s.", ns2_local);
        Hasor.logInfo("ns is %s.", (Object) all_local);
    }
}