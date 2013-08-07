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
package org.hasor.mvc.decorate.parser.freemarker;
import java.io.IOException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import org.hasor.freemarker.FreemarkerManager;
import org.hasor.mvc.decorate.DecorateFilter;
import org.hasor.mvc.decorate.DecorateFilterChain;
import org.hasor.mvc.decorate.DecorateServletRequest;
import org.hasor.mvc.decorate.DecorateServletResponse;
import org.xml.sax.InputSource;
import com.google.inject.Inject;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
/**
 * HTML×°ÊÎÆ÷
 * @version : 2013-6-14
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
public class FreemarkerHtmlParser implements DecorateFilter {
    @Inject
    private FreemarkerManager freemarkerManager = null;
    private String            templateName      = null;
    @Override
    public void doDecorate(DecorateServletRequest request, DecorateServletResponse response, DecorateFilterChain chain) throws IOException {
        byte[] oriData = response.getBufferData();
        response.resetBuffer();
        try {
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(new String(oriData, "utf-8")));
            System.out.println(parser.getDocument().getElementsByTagName("body").toString());
            //
            freemarkerManager.processTemplate(this.templateName, null, response.getWriter());
            //response.getWriter().write(new String(oriData, "utf-8"));
            //response.getWriter().flush();
            //            freemarkerManager.getTemplate(null).process(null,);
            chain.doDecorate(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
            response.getOutputStream().write(oriData);
        }
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.templateName = filterConfig.getInitParameter("templatePath");
    }
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
}