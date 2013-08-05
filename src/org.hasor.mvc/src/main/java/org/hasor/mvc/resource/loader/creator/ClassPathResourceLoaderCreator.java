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
package org.hasor.mvc.resource.loader.creator;
import java.io.IOException;
import org.hasor.Hasor;
import org.hasor.context.AppContext;
import org.hasor.context.XmlProperty;
import org.hasor.mvc.resource.ResourceLoader;
import org.hasor.mvc.resource.ResourceLoaderCreator;
import org.hasor.mvc.resource.ResourceLoaderDefine;
import org.hasor.mvc.resource.loader.ClassPathResourceLoader;
import org.more.util.StringUtils;
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
        Hasor.info("loadClassPath %s", config);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        ClassPathResourceLoader classpathLoader = new ClassPathResourceLoader(config, loader);
        return classpathLoader;
    }
}