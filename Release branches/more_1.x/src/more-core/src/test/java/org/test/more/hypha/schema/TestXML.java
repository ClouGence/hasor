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
package org.test.more.hypha.schema;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.more.core.io.AutoCloseInputStream;
import org.xml.sax.SAXException;
/**
 * 
 * @version 2011-6-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class TestXML {
    public static void main(String[] args) throws SAXException, IOException {
        ArrayList<Source> sourceList = new ArrayList<Source>();
        sourceList.add(new StreamSource(new AutoCloseInputStream(//
                TestXML.class.getResourceAsStream("/org/test/more/hypha/schema/beans-schema.xsd"))));
        sourceList.add(new StreamSource(new AutoCloseInputStream(//
                TestXML.class.getResourceAsStream("/org/test/more/hypha/schema/anno-schema.xsd"))));
        Source[] source = new Source[sourceList.size()];
        sourceList.toArray(source);
        //
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);//建立schema工厂
        Schema schema = schemaFactory.newSchema(source); //利用schema工厂，接收验证文档文件对象生成Schema对象
        Validator validator = schema.newValidator();//通过Schema产生针对于此Schema的验证器，利用students.xsd进行验证
        Source xmlSource = new StreamSource("test_src/org/test/more/hypha/schema/demo-beans-config.xml");//得到验证的数据源
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