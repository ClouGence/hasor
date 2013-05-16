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
package org.platform.freemarker.support;
import org.more.webui.freemarker.loader.ClassPathTemplateLoader;
import org.platform.freemarker.FreemarkerConfig;
import freemarker.template.Configuration;
/**
 * 
 * @version : 2013-5-16
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class DefaultFreemarkerConfiguration implements FreemarkerConfig {
    private Configuration cfg = null;
    @Override
    public Configuration configuration() {
        if (cfg != null)
            return cfg;
        cfg = new Configuration();
        cfg.setTemplateLoader(new ClassPathTemplateLoader(null));
        cfg.setClassicCompatible(true);
        return cfg;
    }
}