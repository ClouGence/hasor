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
import org.more.workflow.context.FormFactory;
import org.more.workflow.context.RunContext;
import org.more.workflow.event.object.NewInstanceEvent;
import org.more.workflow.metadata.ObjectMetadata;
import org.more.workflow.state.AbstractStateHolder;
/**
 * 表单状态操作对象，通过该对象可以方便的保存和载入表单。该类还提供了删除表单的操作。
 * Date : 2010-6-12
 * @author 赵永春
 */
public class FormStateHolder extends AbstractStateHolder {
    private FormMetadata formMetadata;
    /**
     *  创建FormStateHolder对象，这个构造方法需要一个表单的元信息对象作为参数。
     * @param metadataObject 表单的元信息对象
     */
    public FormStateHolder(FormMetadata formMetadata) {
        this.formMetadata = formMetadata;
    };
    @Override
    public ObjectMetadata getMetadata() {
        return this.formMetadata;
    };
    /**
     * 创建{@link FormBean}，注意该方法只会创建一个FormBean类型对象而不会去更新这个Bean的属性。
     * 如果想要完成属性更新请执行updataMode方法。
     */
    @Override
    public FormBean newInstance(RunContext runContext) throws Throwable {
        FormFactory factory = runContext.getApplication().getFormFactory();
        FormBean obj = factory.createForm(this.formMetadata);
        String beanID = factory.generateID(obj);
        //
        obj = new Form(beanID, obj, this);
        NewInstanceEvent event = new NewInstanceEvent(obj, this);
        this.event(event.getEventPhase()[0]);
        return obj;
    };
    /**根据表单ID从持久化系统中装载表单。*/
    public FormBean loadForm(String formID, RunContext runContext) {
        FormFactory factory = runContext.getApplication().getFormFactory();
        FormBean obj = factory.getForm(formID, this.formMetadata);
        //
        obj = new Form(formID, obj, this);
        NewInstanceEvent event = new NewInstanceEvent(obj, this);
        this.event(event.getEventPhase()[0]);
        return obj;
    };
    /**保存表单信息，如果该表单对象已经处于持久化层则该方法将会导致更新操作。*/
    public void saveForm(Form formObject, RunContext runContext) {
        FormBean bean = formObject.getTargetBean();
        String formID = formObject.getID();
        runContext.getApplication().getFormFactory().saveForm(formID, bean);
    };
    /**根据表单ID删除表单，该方法只能删除当前FormMetadata定义的表单对象。*/
    public void deleteFrom(Form formObject, RunContext runContext) {
        String formID = formObject.getID();
        runContext.getApplication().getFormFactory().deleteForm(formID);
    };
};