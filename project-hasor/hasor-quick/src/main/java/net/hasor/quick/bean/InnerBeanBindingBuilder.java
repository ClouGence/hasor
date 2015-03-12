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
package net.hasor.quick.bean;
import java.util.ArrayList;
import net.hasor.core.ApiBinder;
import net.hasor.core.ApiBinder.LinkedBindingBuilder;
import org.more.logger.LoggerHelper;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2015年1月11日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerBeanBindingBuilder implements BeanBindingBuilder {
    private ApiBinder         apiBinder = null;
    private ArrayList<String> names     = new ArrayList<String>();
    public InnerBeanBindingBuilder(ApiBinder apiBinder) {
        this.apiBinder = apiBinder;
    }
    public BeanBindingBuilder aliasName(String aliasName) {
        if (StringUtils.isBlank(aliasName) == false) {
            this.names.add(aliasName);
        }
        return this;
    }
    public <T> LinkedBindingBuilder<T> bindType(Class<T> beanType) {
        if (this.names.isEmpty() == true) {
            throw new NullPointerException("the bean name is undefined!");
        }
        String[] aliasNames = this.names.toArray(new String[this.names.size()]);
        LoggerHelper.logInfo("loadBean [%s] to ‘%s’", StringUtils.join(aliasNames, ","), beanType);
        //
        //真实的类型注册
        LinkedBindingBuilder<T> realBeanBuilder = this.apiBinder.bindType(beanType).uniqueName();
        for (String nameItem : this.names) {
            BeanInfo<T> beanInfo = new InnerBeanInfoData<T>(nameItem, aliasNames, realBeanBuilder.toInfo());
            this.apiBinder.bindType(BeanInfo.class).uniqueName().toInstance(beanInfo);
        }
        //返回给调用者继续配置
        return realBeanBuilder;
    }
}