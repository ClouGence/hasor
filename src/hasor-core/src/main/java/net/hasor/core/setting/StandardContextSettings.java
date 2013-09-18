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
import java.util.List;
import net.hasor.Hasor;
import org.more.util.ResourcesUtils;
/**
 * 继承自FileSettings父类，该类自动装载Classpath中所有静态配置文件。
 * 并且自动装载主配置文件（该配置文件应当只有一个）。
 * @version : 2013-9-9
 * @author 赵永春(zyc@hasor.net)
 */
public class StandardContextSettings extends InputStreamSettings {
    /**默认静态配置文件名称*/
    public static final String StaticSettingName = "static-config.xml";
    private URI                settingURI;
    //
    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(String mainSettings) throws IOException {
        URL url = ResourcesUtils.getResource(mainSettings);
        url = Hasor.assertIsNotNull(url);
        try {
            this.settingURI = url.toURI();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        this.refresh();
    }
    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(File mainSettings) throws IOException {
        mainSettings = Hasor.assertIsNotNull(mainSettings);
        this.settingURI = mainSettings.toURI();
        this.refresh();
    }
    /**创建{@link StandardContextSettings}类型对象。*/
    public StandardContextSettings(URI mainSettings) throws IOException {
        Hasor.assertIsNotNull(mainSettings);
        this.settingURI = mainSettings;
        this.refresh();
    }
    /**获取配置文件URI*/
    public URI getSettingURI() {
        return settingURI;
    }
    //
    @Override
    protected void readyLoad() throws IOException {
        super.readyLoad();
        //1.装载所有static-config.xml
        List<URL> streamList = ResourcesUtils.getResources(StaticSettingName);
        if (streamList != null) {
            for (URL resURL : streamList) {
                InputStream stream = ResourcesUtils.getResourceAsStream(resURL);
                Hasor.info("load ‘%s’", resURL);
                this.addStream(stream);
            }
        }
        //2.装载hasor-config.xml
        if (this.settingURI != null) {
            InputStream stream = ResourcesUtils.getResourceAsStream(this.settingURI);
            Hasor.info("load ‘%s’", this.settingURI);
            this.addStream(stream);
        } else
            Hasor.warning("cannot load the root configuration file ‘%s’", this.settingURI);
    }
    @Override
    public void refresh() throws IOException {
        Hasor.info("reload configuration.");
        this.cleanData();
        //
        try {
            this.loadSettings();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}