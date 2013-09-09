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
package net.hasor.context.setting;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import net.hasor.Hasor;
import org.more.util.StringUtils;
import org.more.util.map.DecSequenceMap;
import org.more.xml.XmlParserKitManager;
import org.more.xml.stream.XmlReader;
/***
 * 传入InputStream的方式获取Settings接口的支持。
 * @version : 2013-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class InputStreamSettings extends AbstractIOSettings {
    private String                  settingEncoding = "utf-8";
    private LinkedList<InputStream> pendingStream   = new LinkedList<InputStream>();
    //
    /**创建{@link InputStreamSettings}对象。*/
    public InputStreamSettings() throws IOException, XMLStreamException {
        this(new InputStream[0], null);
    }
    /**创建{@link InputStreamSettings}对象。*/
    public InputStreamSettings(InputStream inStream) throws IOException, XMLStreamException {
        this(inStream, null);
    }
    /**创建{@link InputStreamSettings}对象。*/
    public InputStreamSettings(InputStream[] inStreams) throws IOException, XMLStreamException {
        this(inStreams, null);
    }
    /**创建{@link InputStreamSettings}对象。*/
    public InputStreamSettings(InputStream inStream, String encoding) throws IOException, XMLStreamException {
        this(new InputStream[] { inStream }, encoding);
    }
    /**创建{@link InputStreamSettings}对象。*/
    public InputStreamSettings(InputStream[] inStreams, String encoding) throws IOException, XMLStreamException {
        super();
        Hasor.assertIsNotNull(inStreams);
        for (InputStream ins : inStreams) {
            Hasor.assertIsNotNull(ins);
            this.addStream(ins);
        }
        if (StringUtils.isBlank(encoding) == false)
            this.setSettingEncoding(encoding);
        this.loadSettings();
    }
    //
    //
    /**获取解析配置文件时使用的字符编码。*/
    public String getSettingEncoding() {
        return this.settingEncoding;
    }
    /**设置解析配置文件时使用的字符编码。*/
    public void setSettingEncoding(String encoding) {
        this.settingEncoding = encoding;
    }
    /**将一个输入流添加到待加载处理列表，使用load方法加载待处理列表中的流。
     * 注意：待处理列表中的流一旦装载完毕将会从待处理列表中清除出去。*/
    public void addStream(InputStream stream) {
        if (stream != null)
            if (this.pendingStream.contains(stream) == false)
                this.pendingStream.add(stream);
    }
    /**load装载所有待处理的流，如果没有待处理流则直接return。*/
    public synchronized void loadSettings() throws IOException, XMLStreamException {
        this.readyLoad();//准备装载
        {
            if (this.pendingStream.isEmpty() == true)
                return;
            //构建装载环境
            Map<String, Map<String, Object>> loadTo = this.getNamespaceSettingMap();
            XmlParserKitManager xmlParserKit = this.getXmlParserKitManager(loadTo);
            xmlParserKit.setContext(this);
            String encoding = this.getSettingEncoding();
            InputStream inStream = null;
            //
            while ((inStream = this.pendingStream.pollFirst()) != null) {
                new XmlReader(inStream, encoding).reader(xmlParserKit, null);
                inStream.close();
            }
        }
        this.loadFinish();//完成装载
    }
    /**准备装载*/
    protected void readyLoad() throws IOException {}
    /**完成装载*/
    protected void loadFinish() throws IOException {}
    /**{@link InputStreamSettings}类型不支持该方法，如果调用该方法会得到一个{@link UnsupportedOperationException}类型异常。*/
    public void refresh() throws IOException {
        throw new UnsupportedOperationException();
    }
    //
    private DecSequenceMap<String, Object>   mergeSettingsMap     = new DecSequenceMap<String, Object>();
    private Map<String, Map<String, Object>> namespaceSettingsMap = new HashMap<String, Map<String, Object>>();
    //
    protected Map<String, Map<String, Object>> getNamespaceSettingMap() {
        return namespaceSettingsMap;
    }
    protected DecSequenceMap<String, Object> getSettingsMap() {
        return mergeSettingsMap;
    }
    protected synchronized XmlParserKitManager getXmlParserKitManager(Map<String, Map<String, Object>> loadTo) throws IOException {
        XmlParserKitManager kitManager = super.getXmlParserKitManager(loadTo);
        this.mergeSettingsMap.removeAllMap();
        for (Map<String, Object> ent : loadTo.values())
            this.mergeSettingsMap.addMap(ent);
        return kitManager;
    }
}