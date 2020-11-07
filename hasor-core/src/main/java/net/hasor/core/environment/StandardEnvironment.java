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
package net.hasor.core.environment;
import net.hasor.core.Environment;
import net.hasor.core.aop.AopClassLoader;
import net.hasor.core.setting.AbstractSettings;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.utils.ResourcesUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

/**
 * {@link Environment}接口实现类，继承自{@link AbstractEnvironment}。
 * @version : 2013-9-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class StandardEnvironment extends AbstractEnvironment {
    public StandardEnvironment() throws IOException {
        this(null, (String) null, null, null);
    }

    public StandardEnvironment(Object context) throws IOException {
        this(context, (String) null, null, null);
    }

    public StandardEnvironment(Object context, File mainSettings) throws IOException {
        this(context, mainSettings, null, null);
    }

    public StandardEnvironment(Object context, URL mainSettings) throws IOException {
        this(context, mainSettings, null, null);
    }

    public StandardEnvironment(Object context, String mainSettings) throws IOException {
        this(context, mainSettings, null, null);
    }

    public StandardEnvironment(Object context, URI mainSettings) throws IOException {
        this(context, mainSettings, null, null);
    }

    public StandardEnvironment(Object context, File mainSettings, Map<String, String> frameworkEnvConfig, ClassLoader loader) throws IOException {
        this(context, toURI(mainSettings), frameworkEnvConfig, loader);
    }

    public StandardEnvironment(Object context, URL mainSettings, Map<String, String> frameworkEnvConfig, ClassLoader loader) throws IOException {
        this(context, toURI(mainSettings), frameworkEnvConfig, loader);
    }

    public StandardEnvironment(Object context, String mainSettings, Map<String, String> frameworkEnvConfig, ClassLoader loader) throws IOException {
        this(context, toURI(mainSettings), frameworkEnvConfig, loader);
    }

    public StandardEnvironment(Object context, URI mainSettings, Map<String, String> frameworkEnvConfig, ClassLoader loader) throws IOException {
        this(context, new StandardContextSettings(mainSettings), frameworkEnvConfig, loader);
    }

    public StandardEnvironment(Object context, AbstractSettings mainSettings, Map<String, String> frameworkEnvConfig, ClassLoader loader) {
        super(context, mainSettings);
        logger.debug("create Environment, type = StandardEnvironment, mainSettings = {}", mainSettings);
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        if (!(loader instanceof AopClassLoader)) {
            loader = new AopClassLoader(loader);
        }
        this.setRootLosder(loader);
        this.initEnvironment(frameworkEnvConfig);
    }

    public static URI toURI(Object source) {
        if (source == null) {
            return null;
        }
        //
        try {
            if (source instanceof URI) {
                return (URI) source;
            }
            if (source instanceof URL) {
                return ((URL) source).toURI();
            }
            if (source instanceof File) {
                return ((File) source).toURI();
            }
            if (source instanceof String) {
                URL resource = ResourcesUtils.getResource(source.toString());
                return resource != null ? resource.toURI() : null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ClassCastException(e.getMessage());
        }
        //
        throw new ClassCastException(source.getClass() + " not convert to URI.");
    }
}