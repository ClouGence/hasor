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
package org.platform.freemarker.loader.creator;
import java.io.File;
import java.io.IOException;
import org.more.global.assembler.xml.XmlProperty;
import org.more.util.StringUtils;
import org.platform.context.AppContext;
import org.platform.freemarker.ITemplateLoaderCreator;
import org.platform.freemarker.TemplateLoaderCreator;
import org.platform.freemarker.loader.DirTemplateLoader;
import org.platform.freemarker.loader.ITemplateLoader;
import org.platform.freemarker.loader.resource.IResourceLoader;
import freemarker.cache.FileTemplateLoader;
/**
 * 实现了{@link IResourceLoader}接口的{@link FileTemplateLoader}类。
 * @version : 2011-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
@TemplateLoaderCreator("PathLoader")
public class PathTemplateLoaderCreator implements ITemplateLoaderCreator {
    @Override
    public ITemplateLoader newTemplateLoader(AppContext appContext, XmlProperty xmlConfig) throws IOException {
        String body = xmlConfig.getText();
        body = StringUtils.isBlank(body) ? "" : body;
        File fileBody = new File(body);
        if (fileBody.exists() == false)
            if (fileBody.mkdirs() == false)
                return null;
        DirTemplateLoader dirTemplateLoader = new DirTemplateLoader(fileBody);
        return dirTemplateLoader;
    }
}