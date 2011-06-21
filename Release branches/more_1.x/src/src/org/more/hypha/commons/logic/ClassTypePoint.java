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
package org.more.hypha.commons.logic;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ExpandPoint;
/**
 * 字节码装载扩展点：该扩展点位于<b>类型创建或获取阶段</b>。该扩展点的功能是将字节码对象装载成为Class类型对象。
 * <br/>注意：1.该扩展点在如果挂载了多个{@link ClassBytePoint}扩展点，则扩展点将被依次执行。并且每次执行之后的新类型和字节码数据会被传入第二个扩展点。 
 * <br/>注意：2.假如{@link ClassBytePoint}类型扩展点执行结果返回了一个null，则hypha系统会使用内置的classLoader装载字节码。
 * <br/>扩展点执行顺序：{@link ClassBytePoint}-&gt<i><b>{@link ClassTypePoint}</b></i>-&gt{@link BeforeCreatePoint}-&gt{@link AfterCreatePoint}
 * @version 2011-3-1
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ClassTypePoint extends ExpandPoint<Class<?>> {
    /**
     * 执行扩展方法。第一个参数是上一个扩展点执行的返回结果，第二个参数如下表示。<br/>
     * target  装载的bean类型{@link Class}类型，注意如果该扩展点不做任何事，必须返回该参数数据。<br/>
     * param[0] {@link AbstractBeanDefine}当前所处的bean定义对象。<br/>
     * param[1] {@link ApplicationContext}扩展点所处的上下文。
     */
    public Class<?> doIt(Class<?> target, Object lastReturnObj, Object[] params);
};