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
package net.hasor.freemarker.loader.creator;
import net.hasor.Hasor;
import net.hasor.context.AppContext;
import net.hasor.context.XmlProperty;
import net.hasor.freemarker.FmTemplateLoader;
import net.hasor.freemarker.FmTemplateLoaderCreator;
import net.hasor.freemarker.FmTemplateLoaderDefine;
import net.hasor.freemarker.loader.ClassPathTemplateLoader;
import org.more.util.StringUtils;
/**
* 负责装载Classpath中的模板文件。
* @version : 2011-9-14
* @author 赵永春 (zyc@byshell.org) 
*/
@FmTemplateLoaderDefine(configElement = "ClassPathLoader")
public class ClassPathTemplateLoaderCreator implements FmTemplateLoaderCreator {
    public FmTemplateLoader newTemplateLoader(AppContext appContext, XmlProperty xmlConfig) {
        ClassPathTemplateLoader classpathLoader = null;
        String body = xmlConfig.getText();
        Hasor.info("loadClassPath %s", body);
        if (StringUtils.isBlank(body))
            classpathLoader = new ClassPathTemplateLoader();
        else
            classpathLoader = new ClassPathTemplateLoader(body);
        return classpathLoader;
    }
}