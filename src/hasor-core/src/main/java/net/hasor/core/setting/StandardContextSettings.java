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
package net.hasor.core.setting;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.hasor.core.Hasor;
import org.more.util.ResourcesUtils;
/**
 * 继承自FileSettings父类，该类自动装载Classpath中所有静态配置文件。
 * 并且自动装载主配置文件（该配置文件应当只有一个）。
 * @version : 2013-9-9
 * @author 赵永春(zyc@hasor.net)
 */
public class StandardContextSettings extends InputStreamSettings {
    /**主配置文件名称*/
    public static final String MainSettingName   = "hasor-config.xml";
    /**默认静态配置文件名称*/
    public static final String StaticSettingName = "static-config.xml";
    private URI                settingURI;
    //
    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings() throws IOException, URISyntaxException {
        this(MainSettingName);
    }
    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(String mainSettings) throws IOException, URISyntaxException {
        URL url = ResourcesUtils.getResource(mainSettings);
        this.settingURI = url.toURI();
    }
    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(File mainSettings) throws IOException {
        this.settingURI = mainSettings.toURI();
    }
    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(URI mainSettings) throws IOException {
        this.settingURI = mainSettings;
    }
    /**获取配置文件URI*/
    public URI getSettingURI() {
        return settingURI;
    }
    //
    protected void readyLoad() throws IOException {
        super.readyLoad();
        //1.装载所有static-config.xml
        List<URL> streamList = ResourcesUtils.getResources(StaticSettingName);
        //2.排序，确保位于jar包中的资源排序优先级靠后。
        //因为覆盖加载是顺序的，因此先加载位于jar中的资源。然后覆盖它们。
        Collections.sort(streamList, new Comparator<URL>() {
            public int compare(URL o1, URL o2) {
                String o1p = o1.getProtocol();
                String o2p = o2.getProtocol();
                if (o1p.equals(o2p)) {
                    return 0;
                }
                if (o1p.equals("jar")) {
                    return -1;
                }
                if (o2p.equals("jar")) {
                    return 1;
                }
                return 0;
            }
        });
        if (streamList != null) {
            for (URL resURL : streamList) {
                InputStream stream = ResourcesUtils.getResourceAsStream(resURL);
                Hasor.logInfo("load ‘%s’", resURL);
                this.addStream(stream);
            }
        }
        //2.装载hasor-config.xml
        if (this.settingURI != null) {
            InputStream stream = ResourcesUtils.getResourceAsStream(this.settingURI);
            Hasor.logInfo("load ‘%s’", this.settingURI);
            this.addStream(stream);
        }
    }
    public void refresh() throws IOException {
        Hasor.logInfo("reload configuration.");
        this.cleanData();
        this.loadSettings();
    }
}