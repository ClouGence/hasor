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
import java.io.File;
import java.io.IOException;
import net.hasor.Hasor;
import net.hasor.context.AppContext;
import net.hasor.context.XmlProperty;
import net.hasor.freemarker.FmTemplateLoader;
import net.hasor.freemarker.FmTemplateLoaderCreator;
import net.hasor.freemarker.FmTemplateLoaderDefine;
import net.hasor.freemarker.loader.DirTemplateLoader;
import net.hasor.freemarker.loader.resource.IResourceLoader;
import org.more.util.StringUtils;
/**
 * 实现了{@link IResourceLoader}接口的{@link FileTemplateLoader}类。
 * @version : 2011-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
@FmTemplateLoaderDefine(configElement = "PathLoader")
public class PathTemplateLoaderCreator implements FmTemplateLoaderCreator {
    public FmTemplateLoader newTemplateLoader(AppContext appContext, XmlProperty xmlConfig) throws IOException {
        String body = xmlConfig.getText();
        body = StringUtils.isBlank(body) ? "" : body;
        body = appContext.getEnvironment().evalString(body);
        File fileBody = new File(body);
        if (fileBody.exists() == false)
            if (fileBody.mkdirs() == false)
                return null;
        Hasor.info("loadPath %s -> %s", xmlConfig.getText(), fileBody);
        DirTemplateLoader dirTemplateLoader = new DirTemplateLoader(fileBody);
        return dirTemplateLoader;
    }
}