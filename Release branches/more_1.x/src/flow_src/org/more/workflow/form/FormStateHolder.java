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
    public Object newInstance(RunContext runContext) throws Throwable {
        FormFactory factory = runContext.getApplication().getFormFactory();
        FormBean obj = factory.getFormBean(runContext, this.formMetadata);
        String beanID = factory.generateID(runContext, obj);
        //
        obj = new Form(beanID, obj, this);
        NewInstanceEvent event = new NewInstanceEvent(obj, this);
        this.event(event.getEventPhase()[0]);
        return obj;
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
    /**根据表单ID装载表单。*/
    public Form loadForm(RunContext runContext, String formID) {
        return null;
    };
    /**刷新表单对象，该方法系统会从持久化层直接重新载入所有数据到formObject中。*/
    public void refreshForm(RunContext runContext, Form formObject) {};
    /**保存表单信息，如果该表单对象已经处于持久化层则该方法将会导致更新操作。*/
    public void saveForm(RunContext runContext, Form formObject) {};
    /**根据表单ID删除表单，该方法只能删除当前FormMetadata定义的表单对象。*/
    public void deleteFrom(RunContext runContext, String formID) {}
};