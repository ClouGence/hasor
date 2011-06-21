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
package org.more.hypha.beans.define;
/**
 * RelationBeanDefine类用于定义一个对另外一个bean的引用。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class RelationBeanDefine extends AbstractBaseBeanDefine {
    private String useTemplate = null; //应用的模板
    private String ref         = null; //所引用的Bean名或id
    private String refPackage  = null; //所引用的Bean作用域
    /**返回“RelationBean”。*/
    public String getBeanType() {
        return "RelationBean";
    }
    /**获取bean使用的模板。*/
    public String getUseTemplate() {
        return this.useTemplate;
    };
    /**设置bean使用的模板。*/
    public void setUseTemplate(String useTemplate) {
        this.useTemplate = useTemplate;
    }
    /**获取引用的Bean名。*/
    public String getRef() {
        return this.ref;
    }
    /**设置引用的Bean名。*/
    public void setRef(String ref) {
        this.ref = ref;
    }
    /**获取引用的Bean所属作用域。*/
    public String getRefPackage() {
        return this.refPackage;
    }
    /**设置引用的Bean所属作用域。*/
    public void setRefPackage(String refPackage) {
        this.refPackage = refPackage;
    }
}