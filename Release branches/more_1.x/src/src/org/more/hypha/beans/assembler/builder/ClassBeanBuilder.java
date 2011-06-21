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
package org.more.hypha.beans.assembler.builder;
import java.lang.reflect.Constructor;
import java.util.Collection;
import org.more.hypha.AbstractPropertyDefine;
import org.more.hypha.beans.define.ClassPathBeanDefine;
import org.more.hypha.beans.define.ConstructorDefine;
import org.more.hypha.commons.logic.AbstractBeanBuilder;
import org.more.hypha.commons.logic.EngineLogic;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * {@link ClassPathBeanDefine}类型定义的解析器。
 * @version 2011-2-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class ClassBeanBuilder extends AbstractBeanBuilder<ClassPathBeanDefine> {
    private static ILog log = LogFactory.getLog(ClassBeanBuilder.class);
    /*------------------------------------------------------------------------------*/
    public Class<?> loadType(ClassPathBeanDefine define, Object[] params) throws Throwable {
        String className = define.getSource();
        ClassLoader loader = this.getApplicationContext().getBeanClassLoader();
        Class<?> type = loader.loadClass(className);
        log.debug("load ClassBean type {%0}.", type);
        return type;
    }
    public <O> O createBean(Class<?> classType, ClassPathBeanDefine define, Object[] params) throws Throwable {
        Collection<ConstructorDefine> cdColl = define.getInitParams();
        Object[] objects = this.transform_toObjects(null, cdColl, params);
        Constructor<?>[] cons = classType.getConstructors();
        Constructor<?> invokeC = null;
        for (Constructor<?> c : cons) {
            Class<?>[] cparams = c.getParameterTypes();
            if (cparams.length != objects.length)
                continue;//参数长度不一致，排除
            //3.如果有参数类型不一样的也忽略---1
            boolean isFind = true;
            for (int i = 0; i < cparams.length; i++) {
                Object param_object = objects[i];
                if (param_object == null)
                    continue;
                //
                if (cparams[i].isAssignableFrom(param_object.getClass()) == false) {
                    isFind = false;
                    break;
                }
            }
            //5.如果有参数类型不一样的也忽略---2
            if (isFind == false)
                continue;
            //符合条件执行调用
            invokeC = c;
        }
        log.debug("find Constructor is {%0}", invokeC);
        return (O) invokeC.newInstance(params);
    };
    /*将一组属性转换成对象。*/
    private Object[] transform_toObjects(Object object, Collection<? extends AbstractPropertyDefine> pds, Object[] params) throws Throwable {
        if (pds == null)
            return new Object[0];
        //
        AbstractApplicationContext app = this.getApplicationContext();
        EngineLogic logic = app.getEngineLogic();
        int size = pds.size();
        int index = 0;
        Object[] res = new Object[size];
        for (AbstractPropertyDefine apd : pds) {
            res[index] = logic.getRootParser().parser(object, apd.getMetaData(), null/*该参数无效*/, app);
            index++;
        }
        return res;
    }
};