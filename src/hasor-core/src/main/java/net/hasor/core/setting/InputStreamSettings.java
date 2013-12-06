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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.hasor.core.Hasor;
import net.hasor.core.setting.xml.SaxXmlParser;
/***
 * 传入InputStream的方式获取Settings接口的支持。
 * @version : 2013-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class InputStreamSettings extends AbstractBaseSettings implements IOSettings {
    private LinkedList<InputStream> pendingStream = new LinkedList<InputStream>();
    /**子类决定如何添加资源*/
    protected InputStreamSettings() {}
    //
    /**创建{@link InputStreamSettings}对象。*/
    public InputStreamSettings(InputStream inStream) throws IOException {
        this(new InputStream[] { inStream });
    }
    /**创建{@link InputStreamSettings}对象。*/
    public InputStreamSettings(InputStream[] inStreams) throws IOException {
        Hasor.assertIsNotNull(inStreams);
        if (inStreams.length == 0)
            return;
        for (InputStream ins : inStreams) {
            Hasor.assertIsNotNull(ins);
            this.addStream(ins);
        }
        this.loadSettings();
    }
    //
    //
    /**将一个输入流添加到待加载处理列表，使用load方法加载待处理列表中的流。
     * 注意：待处理列表中的流一旦装载完毕将会从待处理列表中清除出去。*/
    public void addStream(InputStream stream) {
        if (stream != null)
            if (this.pendingStream.contains(stream) == false)
                this.pendingStream.add(stream);
    }
    //
    /**load装载所有待处理的流，如果没有待处理流则直接return。*/
    public synchronized void loadSettings() throws IOException {
        this.readyLoad();//准备装载
        {
            if (this.pendingStream.isEmpty() == true)
                return;
            //构建装载环境
            Map<String, Map<String, Object>> loadTo = new HashMap<String, Map<String, Object>>();
            InputStream inStream = null;
            //
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
                factory.setFeature("http://xml.org/sax/features/namespaces", true);
                SAXParser parser = factory.newSAXParser();
                SaxXmlParser handler = new SaxXmlParser(loadTo);
                while ((inStream = this.pendingStream.pollFirst()) != null) {
                    parser.parse(inStream, handler);
                    inStream.close();
                }
            } catch (Exception e) {
                throw new IOException(e);
            }
            //
            this.cleanData();
            this.getNamespaceSettingMap().putAll(loadTo);
            for (Map<String, Object> ent : loadTo.values())
                this.getSettingsMap().addMap(ent);
        }
        this.loadFinish();//完成装载
    }
    /**准备装载*/
    protected void readyLoad() throws IOException {}
    /**完成装载*/
    protected void loadFinish() throws IOException {}
    /**{@link InputStreamSettings}类型不支持该方法，调用该方法不会起到任何作用。*/
    public void refresh() throws IOException {}
}