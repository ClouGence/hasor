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
package org.platform.servlet.resource.loader.creator;
import java.io.IOException;
import org.more.global.assembler.xml.XmlProperty;
import org.more.util.StringUtils;
import org.platform.Platform;
import org.platform.context.AppContext;
import org.platform.servlet.resource.ResourceLoaderDefine;
import org.platform.servlet.resource.ResourceLoader;
import org.platform.servlet.resource.ResourceLoaderCreator;
import org.platform.servlet.resource.loader.ClassPathResourceLoader;
/**
 * 用于创建一个可以从classpath中获取资源的ResourceLoader。
 * @version : 2013-6-6
 * @author 赵永春 (zyc@byshell.org)
 */
@ResourceLoaderDefine(configElement = "ClasspathLoader")
public class ClassPathResourceLoaderCreator implements ResourceLoaderCreator {
    @Override
    public ResourceLoader newInstance(AppContext appContext, XmlProperty xmlConfig) throws IOException {
        String config = xmlConfig.getText();
        config = StringUtils.isBlank(config) ? "/" : config;
        Platform.info("loadClassPath %s", config);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        ClassPathResourceLoader classpathLoader = new ClassPathResourceLoader(config, loader);
        return classpathLoader;
    }
}