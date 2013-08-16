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
package org.hasor.context.setting;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.hasor.Hasor;
import org.hasor.context.HasorSettingListener;
import org.hasor.context.LifeCycle;
import org.hasor.context.XmlProperty;
import org.more.util.ResourceWatch;
import org.more.util.ResourcesUtils;
import org.more.util.StringUtils;
import org.more.util.map.DecSequenceMap;
import org.more.util.map.Properties;
import org.more.xml.XmlParserKitManager;
import org.more.xml.stream.XmlReader;
/**
 * Settings接口的实现，并且提供了对hasor-config.xml、static-config.xml、config-mapping.properties文件的解析支持。
 * 除此之外还提供了对config.xml配置文件的改变监听（该配置文件应当只有一个）。
 * @version : 2013-4-2
 * @author 赵永春 (zyc@hasor.net)
 */
public class HasorSettings extends AbstractHasorSettings implements LifeCycle {
    public HasorSettings() {}
    public HasorSettings(String mainConfig) throws IOException {
        this.load(mainConfig);
    }
    /*-------------------------------------------------------------------------------------------------------
     * 
     * 载入配置文件 相关方法
     * 
     */
    private String                    mainSettings    = "hasor-config.xml";
    private String                    settingEncoding = "utf-8";
    private Map<String, List<String>> nsDefine        = null;
    private XmlParserKitManager       initKitManager  = null;
    //
    /**获取解析配置文件时使用的字符编码。*/
    public String getSettingEncoding() {
        return this.settingEncoding;
    }
    /**设置解析配置文件时使用的字符编码。*/
    public void setSettingEncoding(String encoding) {
        this.settingEncoding = encoding;
    }
    /**载入Xml命名空间解析器列表*/
    private Map<String, List<String>> loadNsProp() throws IOException {
        if (this.nsDefine != null)
            return this.nsDefine;
        //
        HashMap<String, List<String>> defineStr = new HashMap<String, List<String>>();
        List<URL> nspropURLs = ResourcesUtils.getResources("META-INF/ns.prop");
        if (nspropURLs != null) {
            for (URL nspropURL : nspropURLs) {
                Hasor.info("find ‘ns.prop’ at ‘%s’.", nspropURL);
                InputStream inStream = ResourcesUtils.getResourceAsStream(nspropURL);
                if (inStream != null) {
                    /*载入ns.prop*/
                    Properties prop = new Properties();
                    prop.load(inStream);
                    for (String key : prop.keySet()) {
                        String v = prop.get(key);
                        String k = key.trim();
                        List<String> nsPasser = null;
                        if (defineStr.containsKey(k) == false) {
                            nsPasser = new ArrayList<String>();
                            defineStr.put(k, nsPasser);
                        } else
                            nsPasser = defineStr.get(k);
                        nsPasser.add(v);
                    }
                    /**/
                }
            }
        }
        //
        //        HashMap<URL, List<String>> define = new HashMap<URL, List<String>>();
        //        for (Entry<String, List<String>> ent : defineStr.entrySet())
        //            define.put(new URL(ent.getKey()), ent.getValue());
        Hasor.info("load space ‘%s’.", defineStr);
        this.nsDefine = defineStr;
        return this.nsDefine;
    }
    /**创建解析器*/
    private XmlParserKitManager loadXmlParserKitManager(Map<String, Map<String, Object>> loadTo) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (this.initKitManager != null)
            return this.initKitManager;
        XmlParserKitManager kitManager = new XmlParserKitManager();
        Map<String, List<String>> nsParser = this.loadNsProp();
        for (String xmlNS : nsParser.keySet()) {
            //获取同一个命名空间下注册的解析器
            List<String> xmlParserSet = nsParser.get(xmlNS);
            //创建用于存放命名空间下数据的容器
            Map<String, Object> dataContainer = new HashMap<String, Object>();
            //创建解析器代理，并且将注册的解析器设置到代理上
            InternalHasorXmlParserPropxy nsKit = new InternalHasorXmlParserPropxy(this, dataContainer);
            //加入到返回结果集合中 
            loadTo.put(xmlNS, dataContainer);
            //
            for (String xmlParser : xmlParserSet) {
                Class<?> xmlParserType = Class.forName(xmlParser);
                nsKit.addTarget((HasorXmlParser) xmlParserType.newInstance());
                Hasor.info("add XmlNamespaceParser ‘%s’ on ‘%s’.", xmlParser, xmlNS);
            }
            /*使用XmlParserKitManager实现不同解析器接收不用命名空间事件的支持*/
            String xmlNSStr = null;
            try {
                xmlNSStr = URLDecoder.decode(xmlNS.toString(), "utf-8");
            } catch (Exception e) {
                xmlNSStr = xmlNS.toString();
            }
            kitManager.regeditKit(xmlNSStr, nsKit);
        }
        Hasor.info("XmlParserKitManager created!");
        this.initKitManager = kitManager;
        return kitManager;
    }
    /**将static-config.xml配置文件的内容装载到参数指定的map中，如果存在重复定义做替换合并操作。*/
    protected void loadStaticConfig(Map<String, Map<String, Object>> loadTo) throws IOException {
        final String staticConfig = "static-config.xml";
        //1.装载所有static-config.xml
        try {
            List<URL> streamList = ResourcesUtils.getResources(staticConfig);
            if (streamList != null) {
                for (URL resURL : streamList) {
                    Hasor.info("load ‘%s’", resURL);
                    loadConfig(resURL.toURI(), loadTo);
                }
            }
        } catch (Exception e) {
            Hasor.error("load ‘%s’ error!%s", staticConfig, e);
        }
    }
    /**装载主配置文件动态配置。*/
    protected void loadMainConfig(String mainConfig, Map<String, Map<String, Object>> loadTo) {
        try {
            URL configURL = ResourcesUtils.getResource(mainConfig);
            if (configURL != null) {
                Hasor.info("load ‘%s’", configURL);
                loadConfig(configURL.toURI(), loadTo);
            } else
                Hasor.warning("cannot load the root configuration file ‘%s’", mainConfig);
        } catch (Exception e) {
            Hasor.error("load ‘%s’ error!%s", mainConfig, e);
        }
    }
    /**装载配置映射，参数是参照的映射配置。*/
    protected Map<String, Object> loadMappingConfig(Map<String, Object> referConfig) {
        final String configMapping = "config-mapping.properties";
        Map<String, Object> mappingSettings = new HashMap<String, Object>();
        try {
            List<URL> mappingList = ResourcesUtils.getResources(configMapping);
            if (mappingList != null)
                for (URL url : mappingList) {
                    InputStream inputStream = ResourcesUtils.getResourceAsStream(url);
                    Properties prop = new Properties();
                    prop.load(inputStream);
                    for (String key : prop.keySet()) {
                        String $propxyKey = key.toLowerCase();
                        String $key = prop.get(key).toLowerCase();
                        Object value = referConfig.get($key);
                        if (value == null) {
                            Hasor.warning("%s mapping to %s value is null.", $propxyKey, $key);
                            continue;
                        }
                        value = (value instanceof XmlProperty) ? ((XmlProperty) value).getText() : value;
                        /*忽略冲突的映射*/
                        if (referConfig.containsKey($propxyKey) == true) {
                            Hasor.error("mapping conflict! %s has this key.", $propxyKey);
                        } else
                            mappingSettings.put($propxyKey, value);
                    }
                }
        } catch (Exception e) {
            Hasor.error("load ‘%s’ error!%s", configMapping, e);
        }
        return mappingSettings;
    }
    /**loadConfig装载配置*/
    private void loadConfig(URI configURI, Map<String, Map<String, Object>> loadTo) throws IOException, XMLStreamException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String encoding = this.getSettingEncoding();
        InputStream stream = ResourcesUtils.getResourceAsStream(configURI);
        //获取解析器
        XmlParserKitManager xmlAccept = null;
        xmlAccept = this.loadXmlParserKitManager(loadTo);
        xmlAccept.setContext(this);
        //解析Xml
        new XmlReader(stream).reader(xmlAccept, encoding, null);
        stream.close();
    }
    /*-------------------------------------------------------------------------------------------------------
     * 
     * 配置文件监听改变事件 相关方法
     * 
     */
    private DecSequenceMap<String, Object>   settingMap          = new DecSequenceMap<String, Object>();
    private Map<String, Map<String, Object>> settingNsMap        = new HashMap<String, Map<String, Object>>();
    private List<HasorSettingListener>       settingListenerList = new ArrayList<HasorSettingListener>();
    private ResourceWatch                    watch               = null;
    //
    protected Map<String, Object> getSettingMap() {
        return this.settingMap;
    }
    @Override
    public String[] getNamespaceArray() {
        return this.nsDefine.keySet().toArray(new String[this.nsDefine.size()]);
    }
    @Override
    public AbstractHasorSettings getNamespace(String namespace) {
        final HasorSettings setting = this;
        final Map<String, Object> data = this.settingNsMap.get(namespace);
        if (data == null)
            return null;
        return new AbstractHasorSettings() {
            @Override
            public void removeSettingsListener(HasorSettingListener listener) {
                throw new UnsupportedOperationException();
            }
            @Override
            public void refresh() throws IOException {
                throw new UnsupportedOperationException();
            }
            @Override
            public void addSettingsListener(HasorSettingListener listener) {
                throw new UnsupportedOperationException();
            }
            @Override
            public void load(String mainConfig) throws IOException {
                throw new UnsupportedOperationException();
            }
            @Override
            public HasorSettingListener[] getSettingListeners() {
                return setting.getSettingListeners();
            }
            @Override
            public AbstractHasorSettings getNamespace(String namespace) {
                return setting.getNamespace(namespace);
            }
            @Override
            public String[] getNamespaceArray() {
                return setting.getNamespaceArray();
            }
            @Override
            protected Map<String, Object> getSettingMap() {
                return data;
            }
        };
    }
    @Override
    public synchronized void load(String mainConfig) throws IOException {
        if (StringUtils.isBlank(mainConfig))
            return;
        this.mainSettings = mainConfig;
        this.refresh();
    }
    @Override
    public synchronized void refresh() throws IOException {
        this.settingMap.removeAllMap();
        Map<String, Map<String, Object>> finalSettings = new HashMap<String, Map<String, Object>>();
        //1.载入并且初始化‘ns.prop’
        try {
            this.loadXmlParserKitManager(finalSettings);
        } catch (Exception e) {
            throw new IOException(e);
        }
        //2.载入静态配置文件
        this.loadStaticConfig(finalSettings);
        //3.载入主配置文件
        this.loadMainConfig(this.mainSettings, finalSettings);
        this.settingNsMap = finalSettings;
        //4.合并不同命名空间下的配置项
        for (Map<String, Object> ent : this.settingNsMap.values())
            this.settingMap.addMap(ent);
        //5.取得映射结果
        Map<String, Object> finalMapping = this.loadMappingConfig(this.settingMap);
        this.settingMap.addMap(finalMapping);
        //6.引发事件
        this.doEvent();
    }
    /**启动配置文件修改监听*/
    public synchronized void start() {
        if (this.watch != null)
            return;
        //3.发送事件
        this.doEvent();
        //1.建立主配置文件监听器
        final HasorSettings settings = this;
        this.watch = new ResourceWatch() {
            public void firstStart(URI resourceURI) throws IOException {}
            /**当配置文件被检测到有修改迹象时，调用刷新进行重载。*/
            public final void onChange(URI resourceURI) throws IOException {
                settings.refresh();
            }
            /**检测主配置文件是否被修改*/
            public long lastModify(URI resourceURI) throws IOException {
                if ("file".equals(resourceURI.getScheme()) == true)
                    return new File(resourceURI).lastModified();
                return 0;
            }
        };
        //2.启动监听器
        try {
            URL configURL = ResourcesUtils.getResource(this.mainSettings);
            if (configURL == null) {
                Hasor.warning("Can't get to mainConfig ‘%s’.", this.mainSettings);
                return;
            }
            this.watch.setName("MasterConfiguration-Watch");
            this.watch.setResourceURI(configURL.toURI());
            this.watch.setDaemon(true);
            Hasor.warning("settings Watch started thread name is %s.", this.watch.getName());
            this.watch.start();
        } catch (Exception e) {
            Hasor.error("settings Watch start error, on : %s Settings file !%s", this.mainSettings, e);
            this.watch = null;
        }
    }
    /**停止配置文件修改监听*/
    public synchronized void stop() {
        if (this.watch == null)
            return;
        this.watch.stop();
        this.watch = null;
    }
    @Override
    public boolean isRunning() {
        return this.watch != null;
    }
    /**触发配置文件重载事件。*/
    protected void doEvent() {
        for (HasorSettingListener listener : this.settingListenerList)
            listener.onLoadConfig(this);
    }
    /**添加配置文件变更监听器。*/
    @Override
    public void addSettingsListener(HasorSettingListener settingsListener) {
        if (this.settingListenerList.contains(settingsListener) == false)
            this.settingListenerList.add(settingsListener);
    }
    /**删除配置文件监听器。*/
    @Override
    public void removeSettingsListener(HasorSettingListener settingsListener) {
        if (this.settingListenerList.contains(settingsListener) == true)
            this.settingListenerList.remove(settingsListener);
    }
    @Override
    public HasorSettingListener[] getSettingListeners() {
        return this.settingListenerList.toArray(new HasorSettingListener[this.settingListenerList.size()]);
    }
}