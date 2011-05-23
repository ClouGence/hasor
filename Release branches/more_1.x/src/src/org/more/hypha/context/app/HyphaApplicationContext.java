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
import org.more.hypha.ApplicationContext;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.hypha.context.AbstractDefineResource;
import org.more.hypha.context.xml.XmlDefineResource;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 简单的{@link ApplicationContext}接口实现类。
 * Date : 2011-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class HyphaApplicationContext extends AbstractApplicationContext {
    private static ILog            log            = LogFactory.getLog(HyphaApplicationContext.class);
    private AbstractDefineResource defineResource = null;
    // 
    public HyphaApplicationContext() throws Throwable {
        super(null);
        log.info("init HyphaApplicationContext start...");
        XmlDefineResource adr = new XmlDefineResource();//必须用XmlDefineResource否则beans可能无法被装载。
        adr.loadDefine();
        this.defineResource = adr;
        log.info("init HyphaApplicationContext started!");
    }
    public HyphaApplicationContext(AbstractDefineResource defineResource) throws Throwable {
        super(null);
        if (defineResource == null)
            throw new NullPointerException("参数defineResource没有指定一个有效的值，该参数不可以为空。");
        if (defineResource.isReady() == false)
            defineResource.toReady();
        this.defineResource = defineResource;
    }
    public AbstractDefineResource getBeanResource() {
        return this.defineResource;
    }
};