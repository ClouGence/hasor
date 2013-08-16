/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.hasor.freemarker.function;
import java.io.IOException;
import java.util.Map;
import javax.inject.Inject;
import org.hasor.freemarker.FmMethod;
import org.hasor.freemarker.FreemarkerManager;
import freemarker.template.TemplateException;
/**
 * 
 * @version : 2013-7-22
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class FnTemplate {
    @Inject
    private FreemarkerManager fmContext;
    //
    @FmMethod("fnStrTemplate")
    public String fnStrTemplate(String stringBody, Map<String, Object> params) throws TemplateException, IOException {
        return this.fmContext.processString(stringBody, params);
    }
    @FmMethod("fnNull")
    public boolean fnNull(Object body) throws TemplateException, IOException {
        return body == null;
    }
}