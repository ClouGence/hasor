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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.more.global.assembler.xml.XmlProperty;
import org.more.util.StringUtils;
import org.platform.Platform;
import org.platform.context.AppContext;
import org.platform.view.freemarker.ITemplateLoaderCreator;
import org.platform.view.freemarker.TemplateLoaderCreator;
import org.platform.view.freemarker.loader.DirTemplateLoader;
import org.platform.view.freemarker.loader.ITemplateLoader;
import org.platform.view.freemarker.loader.resource.IResourceLoader;
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
        body = this.getPath(body);
        File fileBody = new File(body);
        if (fileBody.exists() == false)
            if (fileBody.mkdirs() == false)
                return null;
        Platform.info("loadPath %s -> %s", xmlConfig.getText(), fileBody);
        DirTemplateLoader dirTemplateLoader = new DirTemplateLoader(fileBody);
        return dirTemplateLoader;
    }
    private String getPath(String stringBody) {
        Pattern keyPattern = Pattern.compile("(?:\\{(\\w+)\\}){1,1}");//  (?:\{(\w+)\})
        Matcher keyM = keyPattern.matcher(stringBody);
        ArrayList<String> data = new ArrayList<String>();
        while (keyM.find()) {
            String varKey = keyM.group(1);
            String var = System.getProperty(varKey);
            var = StringUtils.isBlank(var) ? System.getenv(varKey) : var;
            var = var == null ? "" : var;
            data.add(new String(var));
        }
        String[] splitArr = keyPattern.split(stringBody);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < splitArr.length; i++) {
            sb.append(splitArr[i]);
            if (data.size() > i)
                sb.append(data.get(i));
        }
        return sb.toString().replace("/", File.separator);
    }
}