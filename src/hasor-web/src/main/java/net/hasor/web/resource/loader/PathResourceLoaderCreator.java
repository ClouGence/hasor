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
package net.hasor.web.resource.loader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.hasor.Hasor;
import net.hasor.core.AppContext;
import net.hasor.core.XmlNode;
import net.hasor.web.resource.ResourceLoader;
import net.hasor.web.resource.ResourceLoaderCreator;
import net.hasor.web.resource.ResourceLoaderDefine;
import org.more.util.StringUtils;
import org.more.util.io.AutoCloseInputStream;
/**
 * 用于创建一个可以从classpath中获取资源的ResourceLoader。
 * @version : 2013-6-6
 * @author 赵永春 (zyc@hasor.net)
 */
@ResourceLoaderDefine(configElement = "PathLoader")
public class PathResourceLoaderCreator implements ResourceLoaderCreator {
    public ResourceLoader newInstance(AppContext appContext, XmlNode xmlConfig) throws IOException {
        String body = xmlConfig.getText();
        body = StringUtils.isBlank(body) ? "" : body;
        body = appContext.getEnvironment().evalString(body);
        File fileBody = new File(body);
        if (fileBody.exists() == false)
            if (fileBody.mkdirs() == false)
                return null;
        Hasor.info("loadPath %s -> %s", xmlConfig.getText(), fileBody);
        PathResourceLoader dirTemplateLoader = new PathResourceLoader(fileBody.getAbsolutePath());
        return dirTemplateLoader;
    }
}
/**
 * 将一个File对象所代表的路径作为根路径，资源获取相对于该路径下。
 * @version : 2011-9-17
 * @author 赵永春 (zyc@hasor.net)
 */
class PathResourceLoader implements ResourceLoader {
    private String dirPath = null;
    public PathResourceLoader(String dirPath) {
        this.dirPath = dirPath;
    }
    public InputStream getResourceAsStream(String resourcePath) {
        String $name = this.dirPath + "/" + resourcePath;
        $name = $name.replaceAll("/{2}", "/");
        File file = new File($name);
        try {
            if (file.exists() && file.isFile())
                return new AutoCloseInputStream(new FileInputStream(file));
        } catch (Exception e) {}
        return null;
    }
}