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
 * 引用已注册的bean定义aop类型
 * @version 2010-9-27
 * @author 赵永春 (zyc@byshell.org)
 */
public class AopDefineInformed extends AopAbstractInformed {
    private String refBean = null; //连接的aopBean
    /**获取关联的refBean。*/
    public String getRefBean() {
        return this.refBean;
    }
    /**设置关联的RefBean。*/
    public void setRefBean(String refBean) {
        this.refBean = refBean;
    }
}