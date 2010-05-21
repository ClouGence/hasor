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
package org.more.workflow.metadata;
import org.more.workflow.context.ELContext;
import org.more.workflow.state.StateHolder;
/**
 * 更新模型接口，该接口允许模型对象将一些新值更新到自身上。<br/>
 * 所有模型都直接或间接继承{@link AbstractObject}抽象类，模型可以通过该类提供的getMetadata方法
 * 获取到模型metadata对象。这个模型{@link AbstractMetadata metadata}中保存了所有属性映射信息而且
 * {@link AbstractMetadata metadata}对象提供了{@link ModeUpdataHolder}接口的具体实现。<br/>
 * 当然你也可以通过获取模型的{@link StateHolder}然后在{@link StateHolder}上调用updataMode。
 * 无论是{@link StateHolder}对象的updataMode还是{@link AbstractMetadata metadata}的updataMode
 * 它们的作用是一样的。只不过{@link StateHolder}的updataMode使用的是metadata的updataMode方法实现。<br/>
 * Date : 2010-5-16
 * @author 赵永春
 */
public interface ModeUpdataHolder {
    /**更新模型的信息，mode参数决定了要更新的模型对象，而elContext参数则决定了当更新模型时所依赖的elContext。
     * @throws Throwable */
    public void updataMode(Object mode, ELContext elContext) throws Throwable;
};