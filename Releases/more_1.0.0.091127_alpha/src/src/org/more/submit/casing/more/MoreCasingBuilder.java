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
import java.io.File;
import org.more.beans.BeanFactory;
import org.more.beans.BeanResource;
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.resource.XmlFileResource;
import org.more.submit.ActionFactory;
import org.more.submit.CasingBuild;
import org.more.submit.FilterFactory;
/**
 * 
 * <br/>Date : 2009-11-21
 * @author 赵永春
 */
public class MoreCasingBuilder extends CasingBuild {
    //========================================================================================Field
    private MoreActionFactory actionFactory = null;
    private MoreFilterFactory filterFactory = null;
    private BeanFactory       factory       = null;
    //==================================================================================Constructor
    public MoreCasingBuilder(BeanFactory factory) {
        if (factory == null)
            throw new NullPointerException("factory参数不能为空。");
        this.factory = factory;
    }
    public MoreCasingBuilder(String configFile) throws Exception {
        this.factory = new ResourceBeanFactory(new XmlFileResource(configFile), null);
    }
    public MoreCasingBuilder(File configFile) throws Exception {
        this.factory = new ResourceBeanFactory(new XmlFileResource(configFile), null);
    }
    public MoreCasingBuilder(BeanResource resource, ClassLoader loader) throws Exception {
        this.factory = new ResourceBeanFactory(resource, loader);
    }
    //==========================================================================================Job
    @Override
    public ActionFactory getActionFactory() {
        if (this.actionFactory == null)
            this.actionFactory = new MoreActionFactory(factory);
        return actionFactory;
    }
    @Override
    public FilterFactory getFilterFactory() {
        if (this.filterFactory == null)
            this.filterFactory = new MoreFilterFactory(factory);
        return filterFactory;
    }
}