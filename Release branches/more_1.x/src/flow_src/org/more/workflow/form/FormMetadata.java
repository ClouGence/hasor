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
import org.more.CastException;
import org.more.workflow.context.ELContext;
import org.more.workflow.context.RunContext;
import org.more.workflow.event.object.NewInstanceEvent;
import org.more.workflow.metadata.AbstractMetadata;
/**
 * 该类是表述一个流程表单的元信息对象，通过该类可以创建一个{@link FormBean}类型对象。
 * Date : 2010-5-22
 * @author 赵永春
 */
public class FormMetadata extends AbstractMetadata {
    //========================================================================================Field
    private final Class<? extends FormBean> formType; //FormBean的具体类型
    //==================================================================================Constructor
    /**
     * 创建FormMetadata类型对象，参数metadataID决定了FormMetadata对象的元信息ID。formType决定了表单的类型。
     * 如果formType参数为空则会引发{@link NullPointerException}异常。
     * @param metadataID 元信息ID
     * @param formType 表单Bean类型，如果该参数为空则会引发{@link NullPointerException}异常。
     */
    public FormMetadata(String metadataID, Class<? extends FormBean> formType) {
        super(metadataID);
        if (formType == null)
            throw new NullPointerException("没有指定表单元信息所指向的类型。");
        this.formType = formType;
    };
    //==========================================================================================Job
    /**
     * 创建{@link FormBean}，注意该方法只会创建一个FormBean类型对象而不会去更新这个Bean的属性。
     * 如果想要完成属性更新请执行updataMode方法。
     */
    @Override
    public FormBean newInstance(RunContext runContext) throws Throwable {
        NewInstanceEvent event = new NewInstanceEvent(this);
        FormBean obj = this.formType.newInstance();
        this.event(event.getEventPhase()[0]);
        return new Form(null, obj, new FormStateHolder(this));
    };
    /**更新Bean的属性，该方法会依次更新propertyMap中对应的属性。 */
    @Override
    public void updataMode(Object mode, ELContext elContext) throws Throwable {
        if (mode instanceof FormBean == false)
            throw new CastException("无法更新非FormBean类型的模型");
        //------------
        FormBean bean = (FormBean) mode;
        if (mode instanceof Form == true)
            bean = ((Form) mode).getFormBean();
        super.updataMode(bean, elContext);
    };
    /**获取flowForm的具体类型。*/
    public Class<? extends FormBean> getFormType() {
        return this.formType;
    };
};