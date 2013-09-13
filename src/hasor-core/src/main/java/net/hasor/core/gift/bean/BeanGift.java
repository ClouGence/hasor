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
package net.hasor.core.gift.bean;
import java.util.Set;
import net.hasor.Hasor;
import net.hasor.core.ApiBinder;
import net.hasor.core.ApiBinder.BeanBindingBuilder;
import net.hasor.core.gift.InitGift;
import org.more.util.ArrayUtils;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-9-13
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class BeanGift implements InitGift {
    public void loadGift(ApiBinder apiBinder) {
        Set<Class<?>> beanSet = apiBinder.getEnvironment().getClassSet(Bean.class);
        if (beanSet == null)
            return;
        for (Class<?> beanClass : beanSet) {
            Bean annoBean = beanClass.getAnnotation(Bean.class);
            String[] names = annoBean.value();
            if (ArrayUtils.isEmpty(names)) {
                Hasor.warning("missing Bean name %s", beanClass);
                continue;
            }
            if (StringUtils.isBlank(names[0]))
                continue;
            BeanBindingBuilder beanBuilder = apiBinder.newBean(names[0]);
            Hasor.info("loadBean %s bind %s", names, beanClass);
            for (int i = 1; i < names.length; i++)
                beanBuilder.aliasName(names[i]);
            beanBuilder.bindType(beanClass);
        }
    }
}