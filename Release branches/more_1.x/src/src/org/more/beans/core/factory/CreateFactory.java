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
import org.more.beans.BeanFactory;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.CreateTypeEnum;
/**
 * 
 * Date : 2009-11-14
 * @author ’‘”¿¥∫
 */
public class CreateFactory extends CreateEngine {
    private ConstructorCreateEngine constructor = new ConstructorCreateEngine();
    private FactoryCreateEngine     factory     = new FactoryCreateEngine();
    @Override
    public Object newInstance(BeanDefinition definition, Object[] params, BeanFactory context) throws Throwable {
        if (definition.getCreateType() == CreateTypeEnum.New)
            return this.constructor.newInstance(definition, params, context);
        else
            return this.factory.newInstance(definition, params, context);
    }
}
