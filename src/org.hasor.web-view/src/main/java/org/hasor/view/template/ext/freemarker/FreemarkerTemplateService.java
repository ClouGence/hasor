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
package org.hasor.view.template.ext.freemarker;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hasor.freemarker.FreemarkerManager;
import org.hasor.view.template.TemplateException;
import org.hasor.view.template.TemplateService;
/**
* 
* @version : 2013-7-19
* @author ’‘”¿¥∫ (zyc@byshell.org)
*/
public class FreemarkerTemplateService implements TemplateService {
    @Inject
    private volatile FreemarkerManager freemarker = null;
    //
    @Override
    public void processTemplate(String requestURI, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HashMap<String, Object> rootMap = null;
        {
            rootMap = new HashMap<String, Object>();
            rootMap.put("request", request);
            rootMap.put("response", response);
            rootMap.put("session", request.getSession(true));
            Map<String, String[]> reqMap = request.getParameterMap();
            for (Entry<String, String[]> ent : reqMap.entrySet()) {
                String[] values = ent.getValue();
                rootMap.put("req_" + ent.getKey(), (values == null || values.length == 0) ? null : values[0]);
                rootMap.put("req_" + ent.getKey() + "s", values);
            }
            Enumeration<String> reqAtts = request.getAttributeNames();
            while (reqAtts.hasMoreElements()) {
                String name = reqAtts.nextElement();
                rootMap.put(name, request.getAttribute(name));
            }
            HttpSession httpSession = request.getSession(true);
            Enumeration<String> sesAtts = httpSession.getAttributeNames();
            while (sesAtts.hasMoreElements()) {
                String name = sesAtts.nextElement();
                rootMap.put(name, httpSession.getAttribute(name));
            }
        }
        try {
            this.freemarker.processTemplate(requestURI, rootMap, response.getWriter());
        } catch (Exception e) {
            throw new TemplateException(e);
        }
    }
}