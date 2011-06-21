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
import java.io.IOException;
import org.more.hypha.AbstractBeanDefine;
/**
 * 该接口扩展了{@link AbstractBeanBuilder}接口并且提供了两个新方法。值得注意的是如果使用该接口则父接口的
 * loadType方法将会失效。取而代之的是本接口中的loadType重载方法会生效。
 * @version : 2011-5-12
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractBeanBuilderEx<T extends AbstractBeanDefine> extends AbstractBeanBuilder<T> {
    /**
     * 获取目标bean定义的字节码数据。
     * @param define 要装载字节码的Bean定义。
     * @param params 获取bean时候getBean方法传入的参数。
     */
    public abstract ClassData loadBytes(T define, Object[] params) throws IOException;
    /**
     * 将字节码数据装载成类型对象，如果返回null，则使用系统内部的装载器装载字节码。
     * @param bytes 已经装载的字节码数据，只需将该字节码转换为类型即可。
     * @param define 要装载类型的Bean定义。
     * @param params 获取bean时候getBean方法传入的参数。
     */
    public abstract Class<?> loadType(ClassData classData, T define, Object[] params) throws ClassNotFoundException;
    /**失效方法。*/
    public Class<?> loadType(T define, Object[] params) {
        return null;/*如果启用了扩展则,该方法将不在被调用。*/
    }
};