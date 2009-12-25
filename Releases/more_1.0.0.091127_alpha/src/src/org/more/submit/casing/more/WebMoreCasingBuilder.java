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
package org.more.submit.casing.more;
import javax.servlet.ServletContext;
import org.more.InvokeException;
import org.more.beans.BeanFactory;
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.resource.XmlFileResource;
import org.more.submit.ActionFactory;
import org.more.submit.CasingBuild;
import org.more.submit.Config;
import org.more.submit.FilterFactory;
/**
 * 
 * <br/>Date : 2009-11-26
 * @author Administrator
 */
public class WebMoreCasingBuilder extends CasingBuild {
    //========================================================================================Field
    private MoreActionFactory actionFactory = null;
    private MoreFilterFactory filterFactory = null;
    private BeanFactory       factory       = null;
    //==================================================================================Constructor
    /** 使用Web形式构建More的ActionManager，如果在构造方法中没有指定任何参数则当ActionManager在初始化时候回用过init方法获取，SpringContext */
    public WebMoreCasingBuilder() {}
    @Override
    public void init(Config config) throws InvokeException {
        super.init(config);
        System.out.println("init WebMoreCasingBuilder...");
        ServletContext sc = (ServletContext) this.getConfig().getContext();
        String configFile = sc.getRealPath(config.getInitParameter("config"));
        this.factory = new ResourceBeanFactory(new XmlFileResource(configFile), null); //创建对象
        this.actionFactory = new MoreActionFactory(factory);
        this.filterFactory = new MoreFilterFactory(factory);
        System.out.println("init WebMoreCasingBuilder OK");
    }
    @Override
    public ActionFactory getActionFactory() {
        return this.actionFactory;
    }
    @Override
    public FilterFactory getFilterFactory() {
        return this.filterFactory;
    }
}