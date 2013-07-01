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
package org.moreframework.view.decorate.parser;
import java.io.IOException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import org.moreframework.view.decorate.DecorateFilter;
import org.moreframework.view.decorate.DecorateFilterChain;
import org.moreframework.view.decorate.DecorateServletRequest;
import org.moreframework.view.decorate.DecorateServletResponse;
import org.moreframework.view.freemarker.FreemarkerManager;
import com.google.inject.Inject;
import freemarker.template.TemplateException;
/**
 * HTML×°ÊÎÆ÷
 * @version : 2013-6-14
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
public class FreemarkerHtmlParser implements DecorateFilter {
    @Inject
    private FreemarkerManager freemarkerManager = null;
    @Override
    public void doDecorate(DecorateServletRequest request, DecorateServletResponse response, DecorateFilterChain chain) throws IOException {
        byte[] oriData = response.getBufferData();
        response.resetBuffer();
        try {
            freemarkerManager.getTemplate(null).process(null, response.getWriter());
            chain.doDecorate(request, response);
        } catch (Exception e) {
            // TODO: handle exception
            response.getOutputStream().write(oriData);
        }
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub
    }
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
}