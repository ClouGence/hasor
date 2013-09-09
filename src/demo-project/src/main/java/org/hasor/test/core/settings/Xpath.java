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
package org.hasor.test.core.settings;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
public class Xpath {
    public static void main(String[] args) {
        read();
    }
    public static void read() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            InputStream in = Xpath.class.getClassLoader().getResourceAsStream("test.xml");
            Document doc = builder.parse(in);
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            // 选取所有class元素的name属性
            // XPath语法介绍： http://w3school.com.cn/xpath/
            XPathExpression expr = xpath.compile("//class/@name");
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                System.out.println("name = " + nodes.item(i).getNodeValue());
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}