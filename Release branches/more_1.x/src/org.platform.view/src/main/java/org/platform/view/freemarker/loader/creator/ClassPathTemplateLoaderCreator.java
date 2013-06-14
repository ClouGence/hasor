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
package org.platform.view.freemarker.loader.creator;
import org.more.global.assembler.xml.XmlProperty;
import org.more.util.StringUtils;
import org.platform.Platform;
import org.platform.context.AppContext;
import org.platform.view.freemarker.TemplateLoaderCreator;
import org.platform.view.freemarker.FmTemplateLoaderCreator;
import org.platform.view.freemarker.loader.ClassPathTemplateLoader;
import org.platform.view.freemarker.loader.ITemplateLoader;
/**
* 负责装载Classpath中的模板文件。
* @version : 2011-9-14
* @author 赵永春 (zyc@byshell.org) 
*/
@FmTemplateLoaderCreator(configElement = "ClassPathLoader")
public class ClassPathTemplateLoaderCreator implements TemplateLoaderCreator {
    @Override
    public ITemplateLoader newTemplateLoader(AppContext appContext, XmlProperty xmlConfig) {
        ClassPathTemplateLoader classpathLoader = null;
        String body = xmlConfig.getText();
        Platform.info("loadClassPath %s", body);
        if (StringUtils.isBlank(body))
            classpathLoader = new ClassPathTemplateLoader();
        else
            classpathLoader = new ClassPathTemplateLoader(body);
        return classpathLoader;
    }
}