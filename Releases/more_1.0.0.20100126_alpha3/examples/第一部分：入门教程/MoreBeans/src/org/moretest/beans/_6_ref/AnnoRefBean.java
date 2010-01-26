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
package org.moretest.beans._6_ref;
import org.more.beans.BeanFactory;
import org.more.beans.resource.annotation.Bean;
import org.more.beans.resource.annotation.Property;
import org.moretest.beans._1_simple.AnnoSimpleBean;
/**
 * 注解方式配置该Bean，并且注入基本属性类型数据。
 * @version 2010-1-20
 * @author 赵永春 (zyc@byshell.org)
 */
@Bean
public class AnnoRefBean {
    /**anno不支持metaData类型注入。*/
    private String         metaData;
    @Property(refValue = "annoSimpleBean")
    private AnnoSimpleBean annoSimpleBean;
    @Property(refValue = "{@0}")
    private Object         paramData;
    @Property(refValue = "{#apple}")
    private Object         contextData;
    @Property(refValue = "{#this}")
    private BeanFactory    beanFactory;
    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }
    public void setAnnoSimpleBean(AnnoSimpleBean annoSimpleBean) {
        this.annoSimpleBean = annoSimpleBean;
    }
    public void setParamData(Object paramData) {
        this.paramData = paramData;
    }
    public void setContextData(Object contextData) {
        this.contextData = contextData;
    }
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
};