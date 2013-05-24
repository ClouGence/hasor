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
import org.more.util.StringConvertUtils;
import org.more.util.StringUtils;
import org.platform.context.AppContext;
import org.platform.context.WorkSpace;
import org.platform.freemarker.ITemplateLoaderCreator;
import org.platform.freemarker.TemplateLoaderCreator;
import org.platform.freemarker.loader.DirTemplateLoader;
import org.platform.freemarker.loader.ITemplateLoader;
/**
* 处理WorkSpace目录下的路径模板装载。
* @version : 2011-9-14
* @author 赵永春 (zyc@byshell.org) 
*/
@TemplateLoaderCreator("WorkPathLoader")
public class WorkPathTemplateLoaderCreator implements ITemplateLoaderCreator {
    public static enum PathType {
        WorkDir, DataDir, TempDir, CacheDir
    }
    @Override
    public ITemplateLoader newTemplateLoader(AppContext appContext, XmlProperty xmlConfig) throws IOException {
        String pathTypeStr = xmlConfig.getAttributeMap().get("pathType");
        PathType pathType = StringConvertUtils.parseEnum(pathTypeStr, PathType.class, PathType.WorkDir);
        String body = xmlConfig.getText();
        body = StringUtils.isBlank(body) ? "" : body;
        File fileBody = null;
        WorkSpace ws = appContext.getWorkSpace();
        switch (pathType) {
        case WorkDir:
            fileBody = new File(ws.getWorkDir(), body);
            break;
        case DataDir:
            fileBody = new File(ws.getDataDir(), body);
            break;
        case TempDir:
            fileBody = new File(ws.getTempDir(), body);
            break;
        case CacheDir:
            fileBody = new File(ws.getCacheDir(), body);
            break;
        }
        if (fileBody.exists() == false) {
            if (fileBody.mkdirs() == false)
                return null;
            DirTemplateLoader dirTemplateLoader = new DirTemplateLoader(fileBody);
            return dirTemplateLoader;
        }
        return null;
    }
}