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
package net.hasor.core.context;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import net.hasor.core.BindInfoFactoryCreater;
/**
 * 
 * @version : 2014-5-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class StandardAppContext extends AbstractResourceAppContext {
    /**设置主配置文件*/
    public StandardAppContext() throws IOException, URISyntaxException {
        super();
    }
    /**设置主配置文件*/
    public StandardAppContext(final File mainSettings) {
        super(mainSettings);
    }
    /**设置主配置文件*/
    public StandardAppContext(final URI mainSettings) {
        super(mainSettings);
    }
    /**设置主配置文件*/
    public StandardAppContext(final String mainSettings) throws IOException, URISyntaxException {
        super(mainSettings);
    }
    /**设置主配置文件*/
    public StandardAppContext(final BindInfoFactoryCreater registerFactoryCreate) throws IOException, URISyntaxException {
        super();
        this.setRegisterFactoryCreater(registerFactoryCreate);
    }
    /**设置主配置文件*/
    public StandardAppContext(final File mainSettings, final BindInfoFactoryCreater registerFactoryCreate) {
        super(mainSettings);
        this.setRegisterFactoryCreater(registerFactoryCreate);
    }
    /**设置主配置文件*/
    public StandardAppContext(final URI mainSettings, final BindInfoFactoryCreater registerFactoryCreate) {
        super(mainSettings);
        this.setRegisterFactoryCreater(registerFactoryCreate);
    }
    /**设置主配置文件*/
    public StandardAppContext(final String mainSettings, final BindInfoFactoryCreater registerFactoryCreate) throws IOException, URISyntaxException {
        super(mainSettings);
        this.setRegisterFactoryCreater(registerFactoryCreate);
    }
}