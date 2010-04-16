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
package org.more.beans;
/**
 * 这个接口是More的Bean运行环境接口，目前版本beans可以通过该接口获取与beanFactory有关系的父容器。
 * @version 2010-2-26
 * @author 赵永春 (zyc@byshell.org)
 */
public interface BeanContext extends BeanFactory {
    /**获取这个运行环境的父级环境。*/
    public BeanContext getParent();
}