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
package org.test.more.beans.schema;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.more.beans.resource.XmlFileResource;
import org.more.core.io.AutoCloseInputStream;
import org.xml.sax.SAXException;
public class TestXML {
    /**
     * @param args
     */
    public static void main(String[] args) throws SAXException, IOException {
        InputStream in = new AutoCloseInputStream(XmlFileResource.class.getResourceAsStream("/META-INF/beans-xsl-list"));
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String str = null;
        ArrayList<Source> sourceList = new ArrayList<Source>();
        while ((str = br.readLine()) != null) {
            InputStream xsdIn = XmlFileResource.class.getResourceAsStream(str);
            if (xsdIn != null)
                sourceList.add(new StreamSource(xsdIn));
        }
        Source[] source = new Source[sourceList.size()];
        sourceList.toArray(source);
        //
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);//建立schema工厂
        Schema schema = schemaFactory.newSchema(source); //利用schema工厂，接收验证文档文件对象生成Schema对象
        Validator validator = schema.newValidator();//通过Schema产生针对于此Schema的验证器，利用students.xsd进行验证
        Source xmlSource = new StreamSource("test_src/demo-beans-config.xml");//得到验证的数据源
        //开始验证，成功输出success!!!，失败输出fail
        try {
            validator.validate(xmlSource);
            System.out.println("success!!!");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("fail");
        }
    }
}
