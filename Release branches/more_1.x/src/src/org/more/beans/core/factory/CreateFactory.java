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
package org.more.beans.core.factory;
import org.more.DoesSupportException;
import org.more.beans.BeanFactory;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.CreateTypeEnum;
/**
 * more.beans的一个可独立运行子系统，它提供了【Factory、New】两种创建方式。<br/>
 * 该系统是more.beans所有bean的创建器件，Factory创建方式是由FactoryCreateEngine类实现的、New方式则是由ConstructorCreateEngine类实现的。
 * 在more.beans中预定义了Factory、New两种对象创建方式。<br/>
 * CreateFactory的功能是根据bean的配置自动选择Factory方式创建还是使用New方式创建。<br/>
 * Date : 2009-11-14
 * @author 赵永春
 */
public class CreateFactory extends CreateEngine {
    private ConstructorCreateEngine constructor = new ConstructorCreateEngine(); //New方式
    private FactoryCreateEngine     factory     = new FactoryCreateEngine();    //Factory方式
    /** 自动选择创建类型来创建bean对象。 */
    @Override
    public Object newInstance(BeanDefinition definition, Object[] createParams, BeanFactory context) throws Throwable {
        if (definition.getCreateType() == CreateTypeEnum.New)
            return this.constructor.newInstance(definition, createParams, context);
        else if (definition.getCreateType() == CreateTypeEnum.Factory)
            return this.factory.newInstance(definition, createParams, context);
        else
            throw new DoesSupportException("CreateFactory类发现不支持的创建模式。");
    }
}
