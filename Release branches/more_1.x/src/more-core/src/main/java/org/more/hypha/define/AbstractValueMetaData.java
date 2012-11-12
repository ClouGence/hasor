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
package org.more.hypha.define;
/**
 * 表示一个属性值的抽象类
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractValueMetaData extends AbstractDefine<AbstractValueMetaData> implements PropertyMetaTypeEnum {
    private AbstractPropertyDefine forPropertyDefine = null;
    /**获取该元信息所属的属性定义。*/
    public AbstractPropertyDefine getFor() {
        return this.forPropertyDefine;
    }
    public void setFor(AbstractPropertyDefine forPropertyDefine) {
        this.forPropertyDefine = forPropertyDefine;
    };
    /**返回这个属性的属性类型，该类型用于描述属性的类型特征。*/
    public abstract String getMetaDataType();
}