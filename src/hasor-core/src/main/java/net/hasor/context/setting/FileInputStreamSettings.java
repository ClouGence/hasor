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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import net.hasor.Hasor;
import org.more.util.StringUtils;
/***
 * 传入InputStream的方式获取Settings接口的支持。
 * @version : 2013-9-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class FileInputStreamSettings extends InputStreamSettings {
    //
    /**创建{@link FileInputStreamSettings}对象。*/
    public FileInputStreamSettings(String fileName) throws IOException, XMLStreamException {
        this(new filef, null);
    }
    /**创建{@link FileInputStreamSettings}对象。*/
    public FileInputStreamSettings(String[] fileNames) throws IOException, XMLStreamException {
        this(fileNames, null);
    }
    /**创建{@link FileInputStreamSettings}对象。*/
    public FileInputStreamSettings(String fileName, String encoding) throws IOException, XMLStreamException {
        this(new String[] { fileName }, encoding);
    }
    /**创建{@link FileInputStreamSettings}对象。*/
    public FileInputStreamSettings(String[] fileNames, String encoding) throws IOException, XMLStreamException {
        this(inStreams, null);
    }
    /**创建{@link FileInputStreamSettings}对象。*/
    public FileInputStreamSettings(File inStream) throws IOException, XMLStreamException {
        this(new InputStream[] { inStream }, encoding);
        FileInputStream fis;
    }
    /**创建{@link FileInputStreamSettings}对象。*/
    public FileInputStreamSettings(InputStream[] inStreams, String encoding) throws IOException, XMLStreamException {
        super();
        Hasor.assertIsNotNull(inStreams);
        for (InputStream ins : inStreams) {
            Hasor.assertIsNotNull(ins);
            this.addStream(ins);
        }
        if (StringUtils.isBlank(encoding) == false)
            this.setSettingEncoding(encoding);
        this.loadStreams();
    }
    /**{@link FileInputStreamSettings}类型不支持该方法，如果调用该方法会得到一个{@link UnsupportedOperationException}类型异常。*/
    public void refresh() throws IOException {
        //TODO
    }
}