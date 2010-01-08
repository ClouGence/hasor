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
import java.util.HashMap;
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.core.propparser.MainPropertyParser;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.IocTypeEnum;
import org.more.core.task.Task;
/**
 * 根据bean定义自动选择注入方式并且执行注入。Export注入器如果配置了单态模式其对应的{@link ExportInjection}对象会被缓存。
 * InjectionFactory也是一个任务({@link Task})对象,它的任务是清空已经被缓存的{@link ExportInjection}对象。
 * 注意任务执行方法doRun是同步方法。
 * @version 2009-11-9
 * @author 赵永春 (zyc@byshell.org)
 */
public class InjectionFactory extends Task implements Injection {
    //========================================================================================Field
    /**  */
    private static final long                serialVersionUID = 7265406116749265174L;
    /** 被缓存的Export注入 */
    private HashMap<String, ExportInjection> exportMap        = new HashMap<String, ExportInjection>();
    /** Fact方式注入 */
    private FactInjection                    fact;
    /** Ioc方式注入 */
    private IocInjection                     ioc;
    //==================================================================================Constructor
    /**创建一个IocInjection对象，创建时必须指定属性解析器。*/
    public InjectionFactory(MainPropertyParser propParser) {
        if (propParser == null)
            throw new NullPointerException("必须指定propParser参数对象，IocInjection使用这个属性解析器解析属性。");
        /** Fact方式注入 */
        this.fact = new FactInjection();
        /** Ioc方式注入 */
        this.ioc = new IocInjection(propParser);
    }
    //==========================================================================================Job
    /** 根据bean定义自动选择注入方式并且执行注入。Export注入器如果配置了单态模式其对应的{@link ExportInjection}对象会被缓存。 */
    @Override
    public Object ioc(Object object, Object[] params, BeanDefinition definition, ResourceBeanFactory context) throws Exception {
        if (definition.getIocType() == IocTypeEnum.Export) {
            //Export方式，如果Export注入器配置了单态模式会有更好的运行效率。
            String exportName = definition.getExportRefBean();
            ExportInjection exp = null;
            if (this.exportMap.containsKey(exportName) == false) {
                Object exportObj = context.getBean(exportName, params);
                if (exportObj == null || exportObj instanceof ExportInjectionProperty == false)
                    throw new InjectionException("无法装载" + exportName + " Export注入器，或者注入器不可以转换为ExportInjectionProperty类型。");
                exp = new ExportInjection((ExportInjectionProperty) exportObj);
                if (context.isSingleton(exportName) == true)
                    this.exportMap.put(exportName, exp);
            } else
                exp = this.exportMap.get(exportName);
            return exp.ioc(object, params, definition, context);
        } else if (definition.getIocType() == IocTypeEnum.Fact)
            //Fact方式，如果想得到运行效率必须支持BeanDefinition缓存。
            return this.fact.ioc(object, params, definition, context);
        else if (definition.getIocType() == IocTypeEnum.Ioc)
            //传统Ioc，如果想得到运行效率必须支持BeanDefinition缓存。
            return this.ioc.ioc(object, params, definition, context);
        else
            throw new InjectionException("未知注入方式，无法执行注入！");
    }
    /**
     * 负责清理InjectionFactory缓存的{@link ExportInjection}对象，被缓存的{@link ExportInjection}对象其bean定义都是单态对象，
     * 注意该方法是同步方法。当{@link org.more.beans.core.ResourceBeanFactory}调用了clearBeanCache方法时也会激发doRun的执行。
     *  */
    @Override
    protected synchronized void doRun() throws Exception {
        this.exportMap.clear();
    }
}