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
package org.more.beans.resource.annotation.core;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.BeanInterface;
import org.more.beans.info.BeanProperty;
import org.more.beans.info.CreateTypeEnum;
import org.more.beans.info.Prop;
import org.more.beans.info.PropVarValue;
import org.more.beans.resource.annotation.Bean;
import org.more.beans.resource.annotation.util.AnnoContextStack;
import org.more.beans.resource.annotation.util.AnnoProcess;
/**
 * 解析Bean注解
 * @version 2010-1-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class Anno_Bean implements AnnoProcess {
    @Override
    public void beginAnnotation(Annotation anno, Object atObject, AnnoContextStack context) {
        Class<?> atClass = (Class<?>) atObject;
        BeanDefinition bean = (BeanDefinition) context.context;
        Bean annoBean = (Bean) anno;
        /*---name---*/
        if (annoBean.name().equals("") == true) {
            //转换首字母小写
            StringBuffer sb = new StringBuffer(((Class<?>) atObject).getSimpleName());
            char firstChar = sb.charAt(0);
            sb.delete(0, 1);
            sb.insert(0, (char) ((firstChar <= 90) ? firstChar + 32 : firstChar));
            bean.setName(sb.toString());
        } else
            bean.setName(annoBean.name());
        /*---lazyInit---*/
        bean.setLazyInit(annoBean.lazyInit());
        /*---iocType---*/
        bean.setIocType(annoBean.iocType());
        /*---exportRefBean---*/
        bean.setExportRefBean(annoBean.exportRefBean());
        /*---createType---*/
        bean.setCreateType(CreateTypeEnum.New);
        /*---isSingleton---*/
        bean.setSingleton(annoBean.isSingleton());
        /*---aopFiltersRefBean---*/
        String[] aops = annoBean.aopFiltersRefBean();
        if (aops.length != 0)
            bean.setAopFiltersRefBean(aops);
        /*---type---*/
        bean.setPropType(atClass.getName());
        /*---Constructor-Param---*/
        context.setAttribute("constructor", new ArrayList<BeanProperty>());
        /*---property---*/
        context.setAttribute("property", new ArrayList<BeanProperty>());
    }
    @Override
    @SuppressWarnings("unchecked")
    public void endAnnotation(Annotation anno, Object atObject, AnnoContextStack context) {
        Class<?> atClass = (Class<?>) atObject;
        BeanDefinition bean = (BeanDefinition) context.context;
        /*---Constructor-Param---*/
        Constructor<?> c = atClass.getConstructors()[0];
        bean.setConstructorParams(new BeanProperty[c.getParameterTypes().length]);
        ArrayList<BeanProperty> constructorProp = (ArrayList<BeanProperty>) context.getAttribute("constructor");
        //========================================================================Constructor
        Class<?>[] c_params = c.getParameterTypes();
        Annotation[][] c_annoss = c.getParameterAnnotations();
        BeanProperty[] bps = bean.getConstructorParams();
        int j = 0;
        /* constructorProp中保存的只有配置过Param注解的参数。但是实际参数中可能包含多余constructorProp.length数量的参数。
         * 因此下面代码将循环实际的参数，当碰到一个具有Param注解的参数时从constructorProp中取得一个BeanProperty对象。
         * 否则就使用默认策略
         */
        for (int i = 0; i < c_params.length; i++) {
            Class<?> c_param = c_params[i];
            Annotation[] c_annos = c_annoss[i];
            if (c_annos.length != 0) {
                bps[i] = constructorProp.get(j);
                j++;
            } else {
                /*默认策略*/
                bps[i] = new BeanProperty();
                bps[i].setPropType(c_param.getName());
                if (Prop.isBaseType(c_param) == true)
                    bps[i].setRefValue(new PropVarValue(""));
            }
        }
        //========================================================================implInterface
        ArrayList<BeanInterface> implInterfaces = (ArrayList<BeanInterface>) context.getAttribute("implInterface");
        if (implInterfaces != null) {
            BeanInterface[] bis = new BeanInterface[implInterfaces.size()];
            implInterfaces.toArray(bis);
            bean.setImplImplInterface(bis);
        }
        //========================================================================property
        ArrayList<BeanProperty> property = (ArrayList<BeanProperty>) context.getAttribute("property");
        BeanProperty[] props = new BeanProperty[property.size()];
        property.toArray(props);
        bean.setPropertys(props);
    }
}