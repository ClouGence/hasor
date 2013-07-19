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
import java.io.PrintWriter;
import java.util.HashMap;
import org.hasor.view.template.TemplateService;
/**
 * 
 * @version : 2013-7-19
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class FreemarkerTemplateService implements TemplateService {
    @Override
    public void processTemplate(String requestURI, HashMap<String, Object> rootMap, PrintWriter writer) {
        // TODO Auto-generated method stub
    }
    @Override
    public void start() {
        // TODO Auto-generated method stub
    }
    @Override
    public void stop() {
        // TODO Auto-generated method stub
    }
    @Override
    public boolean isRunning() {
        // TODO Auto-generated method stub
        return false;
    }
}