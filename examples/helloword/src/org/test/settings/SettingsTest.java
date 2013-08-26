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
package org.test.settings;
import java.io.IOException;
import org.hasor.context.Settings;
import org.hasor.context.XmlProperty;
import org.hasor.context.core.DefaultAppContext;
import org.more.util.StringUtils;
public class SettingsTest {
    public static void main(String[] args) throws IOException {
        DefaultAppContext context = new DefaultAppContext();
        Settings setting = context.getSettings();
        //
        System.out.println(setting.getString("myProject.name"));//项目名称
        System.out.println(setting.getString("myProject"));//项目信息
        System.out.println(setting.getInteger("userInfo.age"));//年龄
        /*虽然根节点不参与Key/Value转换但是可以获取到它*/
        XmlProperty xmlNode = setting.getXmlProperty("config");
        for (XmlProperty node : xmlNode.getChildren()) {
            if ("userInfo".equals(node.getName())) {
                if ("001".equals(node.getAttributeMap().get("id"))) {
                    System.out.println(node);
                }
            }
        }
    }
}