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
import java.util.ArrayList;
import org.more.beans.BeanFactory;
import org.more.beans.BeanResource;
import org.more.submit.ActionFilter;
import org.more.submit.FilterFactory;
import org.more.util.StringConvert;
/**
 * ActionFactory接口More的支持。
 * <br/>Date : 2009-11-26
 * @author 赵永春
 */
class MoreFilterFactory implements FilterFactory {
    //========================================================================================Field
    private BeanFactory  factory  = null;
    private BeanResource resource = null;
    //==================================================================================Constructor
    public MoreFilterFactory(BeanFactory factory) {
        this.factory = factory;
        this.resource = factory.getBeanResource();
    }
    @Override
    public ActionFilter findFilter(String name) {
        Object obj = this.factory.getBean(name);
        if (obj instanceof ActionFilter)
            return (ActionFilter) obj;
        else
            return null;
    }
    @Override
    public Object findFilterProp(String name, String propName) {
        if (this.resource.containsBeanDefinition(name) == false)
            return null;
        return this.resource.getBeanDefinition(name).getAttribute(propName);
    }
    @Override
    public String[] findPublicFilterNames() {
        ArrayList<String> ns = new ArrayList<String>(0);
        //
        for (String n : this.resource.getBeanDefinitionNames()) {
            Object strIsAction = this.resource.getBeanDefinition(n).getAttribute("isPublicFilter");
            if (strIsAction == null || StringConvert.parseBoolean(strIsAction.toString(), false) == false) {} else
                ns.add(n);
        }
        //
        String[] nsArray = new String[ns.size()];
        ns.toArray(nsArray);
        return nsArray;
    }
    @Override
    public String[] findFilterNames() {
        ArrayList<String> ns = new ArrayList<String>(0);
        //
        for (String n : this.resource.getBeanDefinitionNames()) {
            Object strIsPublicFilter = this.resource.getBeanDefinition(n).getAttribute("isPublicFilter");
            Object strIsFilter = this.resource.getBeanDefinition(n).getAttribute("isFilter");
            boolean boolIsPublicFilter = (strIsPublicFilter == null) ? false : StringConvert.parseBoolean(strIsPublicFilter.toString(), false);
            boolean boolIsFilter = (strIsFilter == null) ? false : StringConvert.parseBoolean(strIsFilter.toString(), false);
            if (boolIsPublicFilter == true || boolIsFilter == true)
                ns.add(n);
        }
        //
        String[] nsArray = new String[ns.size()];
        ns.toArray(nsArray);
        return nsArray;
    }
}
