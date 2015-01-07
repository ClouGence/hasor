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
package net.test.simple.core._11_xml;
import java.io.IOException;
import net.hasor.core.Settings;
import net.hasor.core.setting.InputStreamSettings;
import org.junit.Test;
import org.more.builder.ReflectionToStringBuilder;
import org.more.builder.ToStringStyle;
import org.more.logger.LoggerHelper;
import org.more.util.ResourcesUtils;
/**
 * 对带有命名空间的Xml文件执行读取操作。
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class NameSpaceXmlTest {
    @Test
    public void nameSpaceXmlTest() throws IOException {
        System.out.println("--->>nameSpaceXmlTest<<--");
        InputStreamSettings settings = new InputStreamSettings();
        settings.addStream(ResourcesUtils.getResourceAsStream("net/test/simple/core/_11_xml/ns-all-in-one-config.xml"));
        settings.loadSettings();//装载配置文件
        //
        Settings ns1_settings = settings.getSettings("http://mode1.myProject.net");
        Settings ns2_settings = settings.getSettings("http://mode2.myProject.net");
        //
        String ns1_local = ns1_settings.getString("appSettings.serverLocal.url");
        String ns2_local = ns2_settings.getString("appSettings.serverLocal.url");
        String[] all_local = settings.getStringArray("appSettings.serverLocal.url");
        //
        LoggerHelper.logInfo("ns1 is %s.", ns1_local);
        LoggerHelper.logInfo("ns2 is %s.", ns2_local);
        LoggerHelper.logInfo("ns is %s.", ReflectionToStringBuilder.toString(all_local, ToStringStyle.SIMPLE_STYLE));//同时取得全部命名空间下的相同配置节点配置信息。
    }
}