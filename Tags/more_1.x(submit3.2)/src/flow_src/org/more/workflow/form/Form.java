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
package org.more.workflow.form;
import org.more.workflow.metadata.AbstractObject;
/**
 * 该类是{@link FormBean}接口的一个实现，主要用于代理{@link FormBean}对象，
 * 并且提供元信息和{@link FormStateHolder}的绑定。
 * Date : 2010-5-21
 * @author 赵永春
 */
public class Form extends AbstractObject implements FormBean {
    //========================================================================================Field
    private FormBean formBean = null;
    //==================================================================================Constructor
    protected Form(String objectID, FormBean formBean, FormStateHolder objectStateHolder) {
        super(objectID, objectStateHolder);
        if (formBean == null)
            throw new NullPointerException("创建代理FormBean出现异常，不能创建一个空FormBean引用的代理。");
        this.formBean = formBean;
    };
    //==========================================================================================Job
    /** 获取Form表单对象所代表的那个具体表单实体类。*/
    public FormBean getTargetBean() {
        return this.formBean;
    }
};