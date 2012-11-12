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
 * 表示对另外一个bean的引用，对应的PropertyMetaTypeEnum类型为{@link PropertyMetaTypeEnum#RelationBean}。
 * @version 2010-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class Relation_ValueMetaData extends AbstractValueMetaData {
    private String refBean    = null; //引用的Bean名称
    private String refPackage = null; //引用的Bean所处作用域
    /**该方法将会返回{@link PropertyMetaTypeEnum#RelationBean}。*/
    public String getMetaDataType() {
        return PropertyMetaTypeEnum.RelationBean;
    }
    /**获取引用的bean*/
    public String getRefBean() {
        return this.refBean;
    }
    /**设置引用的bean*/
    public void setRefBean(String refBean) {
        this.refBean = refBean;
    }
    /**获取引用的Bean所处包。*/
    public String getRefPackage() {
        return this.refPackage;
    }
    /**设置引用的Bean所处包。*/
    public void setRefPackage(String refPackage) {
        this.refPackage = refPackage;
    }
}