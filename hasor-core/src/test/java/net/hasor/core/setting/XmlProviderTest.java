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
package net.hasor.core.setting;
import net.hasor.core.Settings;
import net.hasor.core.setting.provider.ConfigSource;
import net.hasor.core.setting.provider.StreamType;
import net.hasor.core.setting.provider.xml.XmlSettingsReader;
import net.hasor.utils.ResourcesUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class XmlProviderTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void xmlTest_1() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL conf = ResourcesUtils.getResource("/net_hasor_core_settings/simple-config.xml");
        ConfigSource configSource = new ConfigSource(StreamType.Xml, conf);
        //
        XmlSettingsReader reader = new XmlSettingsReader();
        BasicSettings settings = new BasicSettings();
        reader.readSetting(classLoader, configSource, settings);
        //
        assert settings.getString("mySelf.myName").equals("赵永春");
        assert settings.getString("myself.myname") == null;
        assert settings.getInteger("mySelf.myAge") == 12;
        assert settings.getString("mySelf.myBirthday").equals("1986-01-01 00:00:00");
        assert settings.getString("mySelf.myWork").equals("Software Engineer");
        assert settings.getString("mySelf.myProjectURL").equals("http://www.hasor.net/");
        assert settings.getString("mySelf.source").equals("Xml");
    }

    @Test
    public void xmlTest_2() throws Exception {
        String data = "" + //
                "<?xml version='1.0' encoding='UTF-8'?>\n" +//
                "<config xmlns='http://www.hasor.net/sechma/main'>\n" +//
                "    <hasor debug='false'>\n" + //
                "        <debug>true</debug>\n" +//
                "    </hasor>\n" +//
                "</config>";
        //
        InputStreamSettings settings = new InputStreamSettings();
        settings.addStringBody(data, StreamType.Xml);
        settings.loadSettings();
        //
        Boolean aBoolean = settings.getBoolean("hasor.debug");
        Boolean[] aBooleanArray = settings.getBooleanArray("hasor.debug");
        assert aBoolean;
        assert aBooleanArray.length == 2;
        assert !aBooleanArray[0];
        assert aBooleanArray[1];
    }

    @Test
    public void xmlTest_3() throws Exception {
        InputStreamSettings settings = new InputStreamSettings();
        settings.addResource("classpath:/net_hasor_core_settings/ns1-config.xml", StreamType.Xml);
        settings.addResource("classpath:/net_hasor_core_settings/ns2-config.xml", StreamType.Xml);
        settings.loadSettings();
        //
        Settings ns1 = settings.getSettings("http://mode1.myProject.net");
        Settings ns2 = settings.getSettings("http://mode2.myProject.net");
        //
        assert ns1.getString("appSettings.serverLocal.url").equals("www.126.com");
        assert ns2.getString("appSettings.serverLocal.url").equals("www.souhu.com");
    }

    @Test
    public void xmlTest_4() throws Exception {
        InputStreamSettings settings = new InputStreamSettings();
        settings.addResource("classpath:/net_hasor_core_settings/ns-all-config.xml", StreamType.Xml);
        settings.loadSettings();
        //
        Settings ns1 = settings.getSettings("http://mode1.myProject.net");
        Settings ns2 = settings.getSettings("http://mode2.myProject.net");
        //
        assert ns1.getString("appSettings.serverLocal.url").equals("www.126.com");
        assert ns2.getString("appSettings.serverLocal.url").equals("www.souhu.com");
    }

    @Test
    public void xmlTest_5() throws Exception {
        InputStreamSettings settings = new InputStreamSettings();
        settings.addResource("classpath:/net_hasor_core_settings/main-ns-config.xml", StreamType.Xml);
        settings.loadSettings();
        //
        assert settings.getString("appSettings.serverLocal.url").equals("www.google.com");
    }
}
