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
import net.hasor.core.context.adapter.RegisterFactory;
/**
 * 
 * @version : 2014-5-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class StandardAppContext extends AbstractConfigResourceAppContext {
    private static final RegisterFactory NullRegister = null;
    /**设置主配置文件*/
    public StandardAppContext() throws IOException, URISyntaxException {
        this(NullRegister);
    }
    /**设置主配置文件*/
    public StandardAppContext(File mainSettings) {
        this(mainSettings, NullRegister);
    }
    /**设置主配置文件*/
    public StandardAppContext(URI mainSettings) {
        this(mainSettings, NullRegister);
    }
    /**设置主配置文件*/
    public StandardAppContext(String mainSettings) throws IOException, URISyntaxException {
        this(mainSettings, NullRegister);
    }
    /**设置主配置文件*/
    public StandardAppContext(RegisterFactory registerManager) throws IOException, URISyntaxException {
        super();
        this.setRegisterContext(registerManager);
    }
    /**设置主配置文件*/
    public StandardAppContext(File mainSettings, RegisterFactory registerManager) {
        super(mainSettings);
        this.setRegisterContext(registerManager);
    }
    /**设置主配置文件*/
    public StandardAppContext(URI mainSettings, RegisterFactory registerManager) {
        super(mainSettings);
        this.setRegisterContext(registerManager);
    }
    /**设置主配置文件*/
    public StandardAppContext(String mainSettings, RegisterFactory registerManager) throws IOException, URISyntaxException {
        super(mainSettings);
        this.setRegisterContext(registerManager);
    }
}