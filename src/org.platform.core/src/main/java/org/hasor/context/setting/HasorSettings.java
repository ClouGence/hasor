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
package org.hasor.context.setting;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.hasor.HasorFramework;
import org.hasor.context.SettingListener;
import org.hasor.context.Settings;
import org.hasor.context.XmlProperty;
import org.more.util.ResourceWatch;
import org.more.util.ResourcesUtils;
import org.more.util.StringConvertUtils;
import org.more.util.StringUtils;
import org.more.util.map.Properties;
import org.more.xml.XmlParserKitManager;
import org.more.xml.stream.XmlReader;
/**
 * Settings接口的实现，并且提供了对config.xml、static-config.xml、config-mapping.properties文件的解析支持。
 * 除此之外还提供了对config.xml配置文件的改变监听（该配置文件应当只有一个）。
 * @version : 2013-4-2
 * @author 赵永春 (zyc@byshell.org)
 */
public class HasorSettings extends ResourceWatch implements Settings {
    //    private DecSequenceMap<String, String>   settingMap   = new DecSequenceMap<String, String>();
    //    private Map<String, Map<String, String>> nsSettingMap = new HashMap<String, Map<String, String>>();
    public HasorSettings() {
        super();
    }
    /*-------------------------------------------------------------------------------------------------------
     * 
     * 载入配置文件 相关方法
     * 
     */
    private String                    mainSettings    = "config.xml";
    private String                    settingEncoding = "utf-8";
    private Map<String, List<String>> nsDefine        = null;
    private XmlParserKitManager       initKitManager  = null;
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
        HashMap<String, List<String>> define = new HashMap<String, List<String>>();
        List<URL> nspropURLs = ResourcesUtils.getResources("ns.prop");
        if (nspropURLs != null) {
            for (URL nspropURL : nspropURLs) {
                HasorFramework.info("find ‘ns.prop’ at ‘%s’.", nspropURL);
                InputStream inStream = ResourcesUtils.getResourceAsStream(nspropURL);
                if (inStream != null) {
                    /*载入ns.prop*/
                    Properties prop = new Properties();
                    prop.load(inStream);
                    for (String key : prop.keySet()) {
                        String v = prop.get(key);
                        String k = key.trim();
                        List<String> nsPasser = null;
                        if (define.containsKey(k) == false) {
                            nsPasser = new ArrayList<String>();
                            define.put(k, nsPasser);
                        } else
                            nsPasser = define.get(k);
                        nsPasser.add(v);
                    }
                    /**/
                }
            }
        }
        HasorFramework.info("load space ‘%s’.", define);
        this.nsDefine = define;
        return this.nsDefine;
    }
    /**创建解析器*/
    private XmlParserKitManager loadXmlParserKitManager(Map<String, Map<String, String>> loadTo) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (this.initKitManager != null)
            return this.initKitManager;
        XmlParserKitManager kitManager = new XmlParserKitManager();
        Map<String, List<String>> nsParser = this.loadNsProp();
        for (String xmlNS : nsParser.keySet()) {
            //获取同一个命名空间下注册的解析器
            List<String> xmlParserSet = nsParser.get(xmlNS);
            //创建用于存放命名空间下数据的容器
            Map<String, String> dataContainer = new HashMap<String, String>();
            //创建解析器代理，并且将注册的解析器设置到代理上
            InternalHasorXmlParserPropxy nsKit = new InternalHasorXmlParserPropxy(dataContainer);
            //加入到返回结果集合中
            loadTo.put(xmlNS, dataContainer);
            //
            for (String xmlParser : xmlParserSet) {
                Class<?> xmlParserType = Class.forName(xmlParser);
                nsKit.addTarget((HasorXmlParser) xmlParserType.newInstance());
                HasorFramework.info("add XmlNamespaceParser ‘%s’ on ‘%s’.", xmlParser, xmlNS);
            }
            /*使用XmlParserKitManager实现不同解析器接收不用命名空间事件的支持*/
            kitManager.regeditKit(xmlNS, nsKit);
        }
        HasorFramework.info("XmlParserKitManager created!");
        this.initKitManager = kitManager;
        return kitManager;
    }
    /**将static-config.xml配置文件的内容装载到参数指定的map中，如果存在重复定义做替换合并操作。*/
    protected void loadStaticConfig(Map<String, Map<String, String>> toMap) throws IOException {
        final String staticConfig = "static-config.xml";
        //1.装载所有static-config.xml
        try {
            List<URL> streamList = ResourcesUtils.getResources(staticConfig);
            if (streamList != null) {
                for (URL resURL : streamList) {
                    loadConfig(resURL.toURI(), toMap);
                    HasorFramework.info("load ‘%s’", resURL);
                }
            }
        } catch (Exception e) {
            HasorFramework.error("load ‘%s’ error!%s", staticConfig, e);
        }
    }
    /**装载主配置文件动态配置。*/
    protected void loadMainConfig(String mainConfig, Map<String, Map<String, String>> toMap) {
        try {
            URL configURL = ResourcesUtils.getResource(mainConfig);
            if (configURL != null) {
                loadConfig(configURL.toURI(), toMap);
                HasorFramework.info("load ‘%s’", configURL);
            }
        } catch (Exception e) {
            HasorFramework.error("load ‘%s’ error!%s", mainConfig, e);
        }
    }
    /**装载配置映射，参数是参照的映射配置。*/
    protected void loadMappingConfig(Map<String, Object> referConfig) {
        final String configMapping = "config-mapping.properties";
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
                            HasorFramework.warning("%s mapping to %s value is null.", $propxyKey, $key);
                            continue;
                        }
                        value = (value instanceof XmlProperty) ? ((XmlProperty) value).getText() : value;
                        /*忽略冲突的映射*/
                        if (referConfig.containsKey($propxyKey) == true) {
                            HasorFramework.error("mapping conflict! %s has this key.", $propxyKey);
                        } else
                            referConfig.put($propxyKey, value);
                    }
                }
        } catch (Exception e) {
            HasorFramework.error("load ‘%s’ error!%s", configMapping, e);
        }
    }
    /**loadConfig装载配置*/
    private void loadConfig(URI configURI, Map<String, Map<String, String>> loadTo) throws IOException, XMLStreamException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String encoding = this.getSettingEncoding();
        InputStream stream = ResourcesUtils.getResourceAsStream(configURI);
        //获取解析器
        XmlParserKitManager xmlAccept = null;
        xmlAccept = this.loadXmlParserKitManager(loadTo);
        xmlAccept.setContext(this);
        //解析Xml
        new XmlReader(stream).reader(xmlAccept, encoding, null);
        //
        //
        //
        //
        //
        //
        //
        //后续还要做数据合并操作
        //
        System.out.println();
        //        Map<String, Map<String, String>> data = xmlAccept.getReturnData();
        //        //
        //        XmlPropertyGlobalFactory xmlg = null;
        //        //1.<载入生效的命名空间>
        //        try {
        //            xmlg = new XmlPropertyGlobalFactory();
        //            xmlg.setIgnoreRootElement(true);/*忽略根*/
        //            /*载入自定义的命名空间支持。*/
        //            if (this.loadNameSpaceList != null && this.loadNameSpaceList.isEmpty() == false)
        //                for (String loadNS : this.loadNameSpaceList)
        //                    if (StringUtils.isBlank(loadNS) == false)
        //                        xmlg.getLoadNameSpace().add(loadNS);
        //            //
        //            Map<String, Object> dataMap = xmlg.createMap(encoding, new Object[] { ResourcesUtils.getResourceAsStream(configURI) });
        //            /*处理多值合并问题（采用覆盖和追加的策略）*/
        //            for (String key : dataMap.keySet()) {
        //                String $key = key.toLowerCase();
        //                Object $var = dataMap.get(key);
        //                Object $varConflict = loadTo.get($key);
        //                if ($varConflict != null && $varConflict instanceof XmlProperty && $var instanceof XmlProperty) {
        //                    XmlProperty $new = (XmlProperty) $var;
        //                    XmlProperty $old = (XmlProperty) $varConflict;
        //                    XmlProperty $final = $old.clone();
        //                    /*覆盖策略*/
        //                    $final.getAttributeMap().putAll($new.getAttributeMap());
        //                    $final.setText($new.getText());
        //                    /*追加策略*/
        //                    List<XmlProperty> $newChildren = new ArrayList<XmlProperty>($new.getChildren());
        //                    List<XmlProperty> $oldChildren = new ArrayList<XmlProperty>($old.getChildren());
        //                    Collections.reverse($newChildren);
        //                    Collections.reverse($oldChildren);
        //                    $final.getChildren().clear();
        //                    $final.getChildren().addAll($oldChildren);
        //                    $final.getChildren().addAll($newChildren);
        //                    Collections.reverse($final.getChildren());
        //                    loadTo.put($key, $final);
        //                } else
        //                    loadTo.put($key, $var);
        //            }
        //        } catch (Exception e) {
        //            HasorFramework.warning("namespcae [%s] no support!", configURI);
        //        }
    }
    public static void main(String[] args) throws IOException {
        HasorSettings settings = new HasorSettings();
        settings.refresh();
    }
    @Override
    public Settings getNamespace(URL namespace) {
        // TODO Auto-generated method stub
        return null;
    }
    private HashMap<String, String> getSettingMap() {
        // TODO Auto-generated method stub
        return null;
    }
    /*-------------------------------------------------------------------------------------------------------
     * 
     * 配置文件监听改变事件 相关方法
     * 
     */
    private final List<SettingListener> settingListenerList = new ArrayList<SettingListener>();
    @Override
    public void refresh() throws IOException {
        Map<String, Map<String, String>> finalSettings = new HashMap<String, Map<String, String>>();
        this.loadStaticConfig(finalSettings);
        this.loadMainConfig(this.mainSettings, finalSettings);
        //        this.loadMappingConfig(finalSettings);
        this.doEvent();
    }
    /**启动配置文件修改监听*/
    @Override
    public synchronized void start() {
        try {
            URL configURL = ResourcesUtils.getResource(this.mainSettings);
            if (configURL == null) {
                HasorFramework.warning("Can't get to mainConfig %s.", configURL);
                return;
            }
            this.setResourceURI(configURL.toURI());
            this.setDaemon(true);
            HasorFramework.warning("settings Watch started thread name is %s.", this.getName());
            super.start();
        } catch (Exception e) {
            HasorFramework.error("settings Watch start error, on : %s Settings file !%s", this.mainSettings, e);
        }
    }
    /**/
    public void firstStart(URI resourceURI) throws IOException {}
    /**当配置文件被检测到有修改迹象时，调用刷新进行重载。*/
    public final void onChange(URI resourceURI) throws IOException {
        this.refresh();
    }
    /**检测主配置文件是否被修改*/
    public long lastModify(URI resourceURI) throws IOException {
        if ("file".equals(resourceURI.getScheme()) == true)
            return new File(resourceURI).lastModified();
        return 0;
    }
    /**触发配置文件重载事件。*/
    protected void doEvent() {
        for (SettingListener listener : this.settingListenerList)
            listener.reLoadConfig(this);
    }
    /**添加配置文件变更监听器。*/
    @Override
    public void addSettingsListener(SettingListener settingsListener) {
        if (this.settingListenerList.contains(settingsListener) == false)
            this.settingListenerList.add(settingsListener);
    }
    /**删除配置文件监听器。*/
    @Override
    public void removeSettingsListener(SettingListener settingsListener) {
        if (this.settingListenerList.contains(settingsListener) == true)
            this.settingListenerList.remove(settingsListener);
    }
    @Override
    public SettingListener[] getSettingListeners() {
        return this.settingListenerList.toArray(new SettingListener[this.settingListenerList.size()]);
    }
    /*-------------------------------------------------------------------------------------------------------
     * 
     * 其他方法
     * 
     */
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public final <T> T getToType(String name, Class<T> toType, T defaultValue) {
        Object oriObject = this.getSettingMap().get(StringUtils.isBlank(name) ? "" : name);
        if (oriObject == null)
            return defaultValue;
        //
        T var = null;
        if (oriObject instanceof String)
            //原始数据是字符串经过Eval过程
            var = StringConvertUtils.changeType((String) oriObject, toType);
        else if (oriObject instanceof GlobalProperty)
            //原始数据是GlobalProperty直接get
            var = ((GlobalProperty) oriObject).getValue(toType, defaultValue);
        else
            //其他类型不予处理（数据就是要的值）
            var = (T) oriObject;
        return var;
    };
    /**解析全局配置参数，并且返回toType参数指定的类型。*/
    public final <T> T getToType(String name, Class<T> toType) {
        return this.getToType(name, toType, null);
    };
    /**解析全局配置参数，并且返回其{@link Object}形式对象。*/
    public Object getObject(String name) {
        return this.getToType(name, Object.class);
    };
    /**解析全局配置参数，并且返回其{@link Object}形式对象。第二个参数为默认值。*/
    public Object getObject(String name, Object defaultValue) {
        return this.getToType(name, Object.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Character}形式对象。*/
    public Character getChar(String name) {
        return this.getToType(name, Character.class);
    };
    /**解析全局配置参数，并且返回其{@link Character}形式对象。第二个参数为默认值。*/
    public Character getChar(String name, Character defaultValue) {
        return this.getToType(name, Character.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link String}形式对象。*/
    public String getString(String name) {
        return this.getToType(name, String.class);
    };
    /**解析全局配置参数，并且返回其{@link String}形式对象。第二个参数为默认值。*/
    public String getString(String name, String defaultValue) {
        return this.getToType(name, String.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。*/
    public Boolean getBoolean(String name) {
        return this.getToType(name, Boolean.class);
    };
    /**解析全局配置参数，并且返回其{@link Boolean}形式对象。第二个参数为默认值。*/
    public Boolean getBoolean(String name, Boolean defaultValue) {
        return this.getToType(name, Boolean.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Short}形式对象。*/
    public Short getShort(String name) {
        return this.getToType(name, Short.class);
    };
    /**解析全局配置参数，并且返回其{@link Short}形式对象。第二个参数为默认值。*/
    public Short getShort(String name, Short defaultValue) {
        return this.getToType(name, Short.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。*/
    public Integer getInteger(String name) {
        return this.getToType(name, Integer.class);
    };
    /**解析全局配置参数，并且返回其{@link Integer}形式对象。第二个参数为默认值。*/
    public Integer getInteger(String name, Integer defaultValue) {
        return this.getToType(name, Integer.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Long}形式对象。*/
    public Long getLong(String name) {
        return this.getToType(name, Long.class);
    };
    /**解析全局配置参数，并且返回其{@link Long}形式对象。第二个参数为默认值。*/
    public Long getLong(String name, Long defaultValue) {
        return this.getToType(name, Long.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Float}形式对象。*/
    public Float getFloat(String name) {
        return this.getToType(name, Float.class);
    };
    /**解析全局配置参数，并且返回其{@link Float}形式对象。第二个参数为默认值。*/
    public Float getFloat(String name, Float defaultValue) {
        return this.getToType(name, Float.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Double}形式对象。*/
    public Double getDouble(String name) {
        return this.getToType(name, Double.class);
    };
    /**解析全局配置参数，并且返回其{@link Double}形式对象。第二个参数为默认值。*/
    public Double getDouble(String name, Double defaultValue) {
        return this.getToType(name, Double.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。*/
    public Date getDate(String name) {
        return this.getToType(name, Date.class);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    public Date getDate(String name, Date defaultValue) {
        return this.getToType(name, Date.class, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象。第二个参数为默认值。*/
    public Date getDate(String name, long defaultValue) {
        return this.getToType(name, Date.class, new Date(defaultValue));
    };
    /**解析全局配置参数，并且返回其{@link Enum}形式对象。第二个参数为默认值。*/
    public <T extends Enum<?>> T getEnum(String name, Class<T> enmType) {
        return this.getToType(name, enmType, null);
    };
    /**解析全局配置参数，并且返回其{@link Enum}形式对象。第二个参数为默认值。*/
    public <T extends Enum<?>> T getEnum(String name, Class<T> enmType, T defaultValue) {
        return this.getToType(name, enmType, defaultValue);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象（用于表示文件）。第二个参数为默认值。*/
    public String getFilePath(String name) {
        return this.getFilePath(name, null);
    };
    /**解析全局配置参数，并且返回其{@link Date}形式对象（用于表示文件）。第二个参数为默认值。*/
    public String getFilePath(String name, String defaultValue) {
        String filePath = this.getToType(name, String.class);
        if (filePath == null || filePath.length() == 0)
            return defaultValue;//空
        //
        int length = filePath.length();
        if (filePath.charAt(length - 1) == File.separatorChar)
            return filePath.substring(0, length - 1);
        else
            return filePath;
    };
    /**解析全局配置参数，并且返回其{@link File}形式对象（用于表示目录）。第二个参数为默认值。*/
    public String getDirectoryPath(String name) {
        return this.getDirectoryPath(name, null);
    };
    /**解析全局配置参数，并且返回其{@link File}形式对象（用于表示目录）。第二个参数为默认值。*/
    public String getDirectoryPath(String name, String defaultValue) {
        String filePath = this.getToType(name, String.class);
        if (filePath == null || filePath.length() == 0)
            return defaultValue;//空
        //
        int length = filePath.length();
        if (filePath.charAt(length - 1) == File.separatorChar)
            return filePath;
        else
            return filePath + File.separatorChar;
    }
    /**解析全局配置参数，并且返回其{@link XmlProperty}形式对象。*/
    public XmlProperty getXmlProperty(String name) {
        return this.getToType(name, XmlProperty.class, null);
    }
}