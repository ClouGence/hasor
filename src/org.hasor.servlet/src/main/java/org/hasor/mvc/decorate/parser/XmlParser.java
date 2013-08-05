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
package org.hasor.mvc.decorate.parser;
import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hasor.view.decorate.DecorateFilter;
import org.hasor.view.decorate.DecorateFilterChain;
/**
 * Xml的Xslt装饰器
 * @version : 2013-6-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlParser implements DecorateFilter {
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, DecorateFilterChain chain) {
        
        Connection c;
        c.
        
        //        String xmlSource = xxxService.loadTopicDefineXmlByCn(topicName);
        //        StreamSource xml = new StreamSource(new StringReader(xmlSource));
        //        StreamSource xsl = new StreamSource(new File("d:\\Tomcat-6\\webapps\\项目\\info\\model.xslt"));
        //        ByteArrayOutputStream resultByte = new ByteArrayOutputStream();
        //        //response.setContentType("text/html; charset=UTF-8"); 
        //        StreamResult result = new StreamResult(resultByte);
        //        Transformer trans = TransformerFactory.newInstance().newTransformer(xsl);
        //        trans.setParameter("entity_name", topicName);
        //        //trans.setOutputProperty("encoding","UTF-8"); 
        //        trans.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "GB2312");
        //        trans.transform(xml, result);
        //        out.print(resultByte.toString());
    }
}