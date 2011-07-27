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
import org.more.workflow.metadata.ObjectMetadata;
/**
 * 该类是表述一个流程表单的元信息对象，通过该类可以创建一个{@link FormBean}类型对象。
 * Date : 2010-5-22
 * @author 赵永春
 */
public class FormMetadata extends ObjectMetadata {
    private final Class<? extends FormBean> formType; //FormBean的具体类型
    /**
    * 创建FormMetadata类型对象，参数metadataID决定了FormMetadata对象的元信息ID。formType决定了表单的类型。
    * 如果formType参数为空则会引发{@link NullPointerException}异常。
    * @param metadataID 元信息ID
    * @param formType 表单Bean类型，如果该参数为空则会引发{@link NullPointerException}异常。
    */
    public FormMetadata(String metadataID, Class<? extends FormBean> formType) {
        super(metadataID);
        if (metadataID == null || formType == null)
            throw new NullPointerException("没有指定元信息ID或者没有指定表单元信息所指向的类型。");
        this.formType = formType;
    };
    /**获取flowForm的具体类型。*/
    public Class<? extends FormBean> getFormType() {
        return this.formType;
    };
};