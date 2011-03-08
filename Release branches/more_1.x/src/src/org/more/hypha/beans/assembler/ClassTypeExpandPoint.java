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
 * 字节码装载扩展点：该扩展点位于<b>类型创建或获取阶段</b>。该扩展点的功能是将字节码对象装载成为Class类型对象。
 * <br/>注意：1.该扩展点在如果挂载了多个{@link ClassByteExpandPoint}扩展点，则扩展点将被依次执行。并且每次执行之后的新类型和字节码数据会被传入第二个扩展点。 
 * <br/>注意：2.假如{@link ClassByteExpandPoint}类型扩展点执行结果返回了一个null，则hypha系统会使用内置的classLoader装载字节码。
 * <br/>扩展点执行顺序：{@link ClassByteExpandPoint}-&gt<i><b>{@link ClassTypeExpandPoint}</b></i>-&gt{@link BeforeCreateExpandPoint}-&gt{@link AfterCreateExpandPoint}
 * @version 2011-3-1
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ClassTypeExpandPoint extends ExpandPoint {
    /**
     * 执行扩展方法。
     * @param beanType 装载的bean类型，注意如果该扩展点不做任何事。必须返回该参数数据。
     * @param tryType 尝试要装载的类型，对于该扩展点首次执行时该参数为空，当执行第二个{@link ClassTypeExpandPoint}类型扩展点时该参数表示为上一个扩展点返回的类型。
     * @param define bean定义。
     * @param context 上下文对象。
     * @return 返回装饰之后的类型数据。
     */
    public Class<?> decorateType(byte[] beanType, Class<?> tryType, AbstractBeanDefine define, ApplicationContext context);
};