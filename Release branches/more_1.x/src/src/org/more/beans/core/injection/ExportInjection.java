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
package org.more.beans.core.injection;
import org.more.beans.BeanFactory;
import org.more.beans.core.Injection;
import org.more.beans.info.BeanDefinition;
/**
 * 使用Export方式可以使开发人员参与属性注入过程，在Export方式下more.beans不会对属性做任何注入操作。
 * more.bean会委托ExportInjectionProperty接口进行注入请求。决定对属性的注入类必须实现
 * ExportInjectionProperty接口。如果外部注入处理对象为空则Export将忽略注入请求。
 * <br/>Export方式注入可以使开发人员参与属性注入过程从而提供更高级的属性注入业务逻辑。这与aop不同，
 * aop专注于对方法进行切面编程，而ioc则专注于属性注入。Export是提供一种更灵活更自由的高级注入方式。<br/>
 * Date : 2009-11-7
 * @author 赵永春
 */
public class ExportInjection implements Injection {
    //========================================================================================Field
    /** 使用的外部属性注入对象。 */
    private ExportInjectionProperty injectionProperty = null;
    //==================================================================================Constructor
    /** 创建一个ExportInjection类型对象 */
    public ExportInjection(ExportInjectionProperty injectionProperty) {
        this.injectionProperty = injectionProperty;
    }
    //==========================================================================================Job
    /** 执行ExportInjectionProperty接口方法请求注入属性。 */
    @Override
    public void ioc(Object object, Object[] params, BeanDefinition definition, BeanFactory context) {
        if (this.injectionProperty != null)
            this.injectionProperty.injectionProperty(object, params, definition, context);
    }
}
