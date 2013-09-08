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
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import net.hasor.Hasor;
import org.more.util.map.DecSequenceMap;
import org.more.xml.XmlParserKitManager;
import org.more.xml.stream.XmlReader;
/***
 * 传入Reader的方式获取Settings接口的支持。
 * @version : 2013-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class ReaderSettings extends AbstractIOSettings {
    private LinkedList<Reader> pendingReader = new LinkedList<Reader>();
    //
    /**创建{@link ReaderSettings}对象。*/
    public ReaderSettings(Reader inReader) throws IOException, XMLStreamException {
        this(new Reader[] { inReader });
    }
    /**创建{@link ReaderSettings}对象。*/
    public ReaderSettings(Reader[] inReaders) throws IOException, XMLStreamException {
        super();
        Hasor.assertIsNotNull(inReaders);
        for (Reader ins : inReaders) {
            Hasor.assertIsNotNull(ins);
            this.addReader(ins);
        }
        this.loadReaders();
    }
    /**将一个输入流添加到待加载处理列表，使用load方法加载待处理列表中的流。
     * 注意：待处理列表中的流一旦装载完毕将会从待处理列表中清除出去。*/
    public void addReader(Reader reader) {
        if (reader != null)
            if (this.pendingReader.contains(reader) == false)
                this.pendingReader.add(reader);
    }
    /**load装载所有待处理的流，如果没有待处理流则直接return。*/
    public final synchronized void loadReaders() throws IOException, XMLStreamException {
        if (this.pendingReader.isEmpty() == true)
            return;
        //构建装载环境
        Map<String, Map<String, Object>> loadTo = this.getNamespaceSettingMap();
        XmlParserKitManager xmlParserKit = this.getXmlParserKitManager(loadTo);
        xmlParserKit.setContext(this);
        Reader inReader = null;
        //
        this.readyLoad();//准备装载
        while ((inReader = this.pendingReader.pollFirst()) != null) {
            new XmlReader(inReader).reader(xmlParserKit, null);
            inReader.close();
        }
        //
        this.loadFinish();//完成装载
    }
    /**准备装载*/
    protected void readyLoad() {}
    /**完成装载*/
    protected void loadFinish() {}
    /**{@link ReaderSettings}类型不支持该方法，如果调用该方法会得到一个{@link UnsupportedOperationException}类型异常。*/
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
    protected Map<String, Object> getSettingsMap() {
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