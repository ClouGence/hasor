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
package org.hasor.servlet.resource.loader.creator;
import java.io.File;
import java.io.IOException;
import org.hasor.MoreFramework;
import org.hasor.context.AppContext;
import org.hasor.servlet.resource.ResourceLoader;
import org.hasor.servlet.resource.ResourceLoaderCreator;
import org.hasor.servlet.resource.ResourceLoaderDefine;
import org.hasor.servlet.resource.loader.PathResourceLoader;
import org.more.global.assembler.xml.XmlProperty;
import org.more.util.StringUtils;
/**
 * 用于创建一个可以从classpath中获取资源的ResourceLoader。
 * @version : 2013-6-6
 * @author 赵永春 (zyc@byshell.org)
 */
@ResourceLoaderDefine(configElement = "PathLoader")
public class PathResourceLoaderCreator implements ResourceLoaderCreator {
    @Override
    public ResourceLoader newInstance(AppContext appContext, XmlProperty xmlConfig) throws IOException {
        String body = xmlConfig.getText();
        body = StringUtils.isBlank(body) ? "" : body;
        body = appContext.getEnvironment().evalString(body);
        File fileBody = new File(body);
        if (fileBody.exists() == false)
            if (fileBody.mkdirs() == false)
                return null;
        MoreFramework.info("loadPath %s -> %s", xmlConfig.getText(), fileBody);
        PathResourceLoader dirTemplateLoader = new PathResourceLoader(fileBody.getAbsolutePath());
        return dirTemplateLoader;
    }
}