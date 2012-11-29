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
package org.more.hypha.context.app;
import java.io.File;
import org.more.hypha.ApplicationContext;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.hypha.context.AbstractDefineResource;
import org.more.hypha.xml.XmlDefineResource;
/**
 * 简单的{@link ApplicationContext}接口实现类。
 * Date : 2011-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class FileApplicationContext extends AbstractApplicationContext {
    private AbstractDefineResource defineResource = null;
    // 
    public FileApplicationContext() throws Throwable {
        this((String) null);
    };
    public FileApplicationContext(String configFile) throws Throwable {
        super(null);
        XmlDefineResource adr = new XmlDefineResource();//必须用XmlDefineResource否则beans可能无法被装载。
        if (configFile != null)
            adr.addSource(new File(configFile));
        adr.loadDefine();
        this.defineResource = adr;
    };
    public FileApplicationContext(File configFile) throws Throwable {
        super(null);
        XmlDefineResource adr = new XmlDefineResource();//必须用XmlDefineResource否则beans可能无法被装载。
        if (configFile != null && configFile.exists() == true)
            adr.addSource(configFile);
        adr.loadDefine();
        this.defineResource = adr;
    };
    public AbstractDefineResource getBeanResource() {
        return this.defineResource;
    };
};