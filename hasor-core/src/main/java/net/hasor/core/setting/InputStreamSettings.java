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
import java.util.LinkedList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import net.hasor.core.setting.xml.SaxXmlParser;
/***
 * 传入{@link InputStream}的方式获取{@link Settings}接口的支持。
 * @version : 2013-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class InputStreamSettings extends AbstractMergeSettings implements IOSettings {
    private LinkedList<InputStream> pendingStream = new LinkedList<InputStream>();
    /**子类决定如何添加资源*/
    public InputStreamSettings() {}
    //
    /**创建{@link InputStreamSettings}对象。*/
    public InputStreamSettings(final InputStream inStream) throws IOException {
        this(new InputStream[] { inStream });
    }
    /**创建{@link InputStreamSettings}对象。*/
    public InputStreamSettings(final InputStream[] inStreams) throws IOException {
        Hasor.assertIsNotNull(inStreams);
        if (inStreams.length == 0) {
            return;
        }
        for (InputStream ins : inStreams) {
            Hasor.assertIsNotNull(ins);
            this.addStream(ins);
        }
    }
    //
    /**将一个输入流添加到待加载处理列表，使用load方法加载待处理列表中的流。
     * 注意：待处理列表中的流一旦装载完毕将会从待处理列表中清除出去。*/
    public void addStream(final InputStream stream) {
        if (stream != null) {
            if (this.pendingStream.contains(stream) == false) {
                this.pendingStream.add(stream);
            }
        }
    }
    //
    /**load装载所有待处理的流，如果没有待处理流则直接return。*/
    @Override
    public synchronized void loadSettings() throws IOException {
        this.readyLoad();//准备装载
        this.cleanData();
        {
            if (this.pendingStream.isEmpty() == true) {
                return;
            }
            //构建装载环境
            InputStream inStream = null;
            //
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
                factory.setFeature("http://xml.org/sax/features/namespaces", true);
                SAXParser parser = factory.newSAXParser();
                SaxXmlParser handler = new SaxXmlParser(this);
                while ((inStream = this.pendingStream.removeFirst()) != null) {
                    parser.parse(inStream, handler);
                    inStream.close();
                    if (this.pendingStream.isEmpty()) {
                        break;
                    }
                }
                super.refresh();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
        this.loadFinish();//完成装载
    }
    /**准备装载*/
    protected void readyLoad() throws IOException {}
    /**完成装载*/
    protected void loadFinish() throws IOException {}
}