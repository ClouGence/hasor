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
import java.util.HashMap;
import java.util.Map;
import org.more.CastException;
import org.more.RepeateException;
import org.more.workflow.context.ELContext;
import org.more.workflow.el.PropertyBinding;
import org.more.workflow.event.EventType;
import org.more.workflow.event.object.NewInstanceEvent;
import org.more.workflow.metadata.AbstractMetadata;
import org.more.workflow.metadata.ModeUpdataHolder;
/**
 * 该类是表述一个流程表单的元信息对象，通过该类可以创建一个{@link FormBean}类型对象。
 * 也可以通过自身保存的属性元信息来更新某个{@link FormBean}模型。
 * 除此之外FormMetadata类型提供了注册移除属性元信息的方法。通过这些方法可以修改FormMetadata元信息对象中的属性。
 * Date : 2010-5-16
 * @author 赵永春
 */
public class FormMetadata extends AbstractMetadata {
    //========================================================================================Field
    private final Class<? extends FormBean>         formType;   //FormBean的具体类型
    private final Map<String, FormPropertyMetadata> propertyMap; //保存用于更新Bean模型时使用的El属性集合
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
        this.propertyMap = new HashMap<String, FormPropertyMetadata>();
    };
    //==========================================================================================Job
    /**创建{@link FormBean}，每一个新创建的{@link FormBean}对象都会执行updataMode方法。*/
    @Override
    public FormBean newInstance(ELContext elContext) throws Throwable {
        FormBean obj = this.formType.newInstance();
        this.updataMode(obj, elContext);//更新模型 
        this.event(new NewInstanceEvent(EventType.NewInstanceEvent, obj));
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
        super.updataMode(mode, elContext);
        //------------
        for (FormPropertyMetadata item : this.propertyMap.values())
            item.updataMode(bean, elContext);
    };
    /**获取flowForm的具体类型。*/
    public Class<? extends FormBean> getFormType() {
        return this.formType;
    };
    /**
     * 添加一个属性元信息，该方法可以指定当前form的一个属性名称，并同时指定其一个表达式。
     * 当调用{@link ModeUpdataHolder}接口方法时FormMetadata会根据注册的属性列表对预更新模型执行更新操作。
     * 注意不能重复注册同一个属性，否则会引发{@link RepeateException}异常。
     * @param propertyName 要添加的属性名。
     * @param expressionString 该属性对应的{@link PropertyBinding 表达式}。
     */
    public void addProperty(String propertyName, String expressionString) {
        if (this.propertyMap.containsKey(propertyName) == true)
            throw new RepeateException("不能注册重复的属性元信息。 ");
        this.addProperty(new FormPropertyMetadata(propertyName, expressionString));
    };
    /**
     * 添加一个属性元信息，该方法可以指定当前form的一个属性名称，并同时指定其一个表达式。当调用
     * {@link ModeUpdataHolder}接口方法时FormMetadata会根据注册的属性列表对预更新模型执行更新操作。
     * 注意不能重复注册同一个属性，否则会引发{@link RepeateException}异常。
     * @param propertyItem 要添加的属性对象该属性对应的{@link PropertyBinding 表达式}。
     */
    public void addProperty(FormPropertyMetadata propertyItem) {
        if (this.propertyMap.containsKey(propertyItem.getMetadataID()) == true)
            throw new RepeateException("不能注册重复的属性元信息。 ");
        this.propertyMap.put(propertyItem.getMetadataID(), propertyItem);
    };
    /**取消一个属性元信息的添加，这里只需要传递属性名即可，对于FormItemMetadata类型属性名就是其metadataID。*/
    public void removeProperty(String propertyName) {
        if (this.propertyMap.containsKey(propertyName) == true)
            this.propertyMap.remove(propertyName);
    };
};