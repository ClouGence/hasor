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
package org.more.hypha.beans.assembler;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ExpandPoint;
import org.more.hypha.beans.AbstractBeanDefine;
/**
 * 预创建扩展点：该扩展点位于<b>对象创建阶段</b>。该扩展点的可以提供一个使外部程序参与Bean类型构造的切口，如果预创建成功则会取消<b>默认创建过程</b>。
 * <br/>注意：1.该扩展点在如果挂载了多个{@link BeforeCreateExpandPoint}扩展点，则扩展点将被依次执行。 直至遇到一个不为空的执行结果。
 * <br/>注意：2.假如{@link BeforeCreateExpandPoint}类型扩展点执行结果返回了一个null，则hypha系统会使用内置的创建过程执行Bean的创建。
 * <br/>扩展点执行顺序：{@link ClassByteExpandPoint}-&gt{@link ClassTypeExpandPoint}-&gt<i><b>{@link BeforeCreateExpandPoint}</b></i>-&gt{@link AfterCreateExpandPoint}
 * @version 2011-3-7
 * @author 赵永春 (zyc@byshell.org)
 */
public interface BeforeCreateExpandPoint extends ExpandPoint {
    /**
     * 执行扩展方法。
     * @param beanType bean定义执行之后所装载的Bean类型。
     * @param params getBean方法所传入的动态参数。
     * @param define bean定义。
     * @param context 扩展点所处的上下文。
     * @return 返回扩展点执行结果。
     */
    public Object beforeCreate(Class<?> beanType, Object[] params, AbstractBeanDefine define, ApplicationContext context);
};