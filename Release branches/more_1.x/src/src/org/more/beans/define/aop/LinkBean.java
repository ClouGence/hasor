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
package org.more.beans.define.aop;
/**
 * 负责连接切点配置和aop监听器的Bean
 * @version 2010-9-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class LinkBean {
    private AbstractPointcutDefine refPointcut = null; //连接的切点
    private String                 refBean     = null; //连接的aopBean名
    /**创建{@link LinkBean}对象*/
    public LinkBean(AbstractPointcutDefine refPointcut, String refBean) {
        this.refPointcut = refPointcut;
        this.refBean = refBean;
    }
    /**获取连接的切点。*/
    public AbstractPointcutDefine getRefPointcut() {
        return refPointcut;
    }
    /**获取连接的aopBean名。*/
    public String getRefBean() {
        return refBean;
    }
}