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
import org.more.beans.resource.annotation.AnnoContextStack;
import org.more.beans.resource.annotation.AnnoProcess;
import org.more.beans.resource.annotation.AnnoScopeEnum;
/**
 * 扫描类获取类名称
 * @version 2010-1-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class Scan_ClassName implements AnnoProcess {
    private String   beanName;
    private Class<?> beanClass;
    private boolean  init;
    @Override
    public void doAnnotation(Annotation anno, Object atObject, AnnoScopeEnum annoScope, AnnoContextStack context) {
        if (AnnoScopeEnum.Anno_Type == annoScope && anno instanceof Bean) {
            Bean beanAnno = (Bean) anno;
            bean = true;
            beanClass = (Class<?>) atObject;
            init = beanAnno.lazyInit();//是否默认装载
            beanName = beanAnno.name();
            if (beanName.equals("") == true) {
                //转换首字母小写
                StringBuffer sb = new StringBuffer(beanClass.getSimpleName());
                char firstChar = sb.charAt(0);
                sb.delete(0, 1);
                sb.insert(0, (char) ((firstChar <= 90) ? firstChar + 32 : firstChar));
                this.beanName = sb.toString();
            }
        }
    }
    public void reset() {
        beanName = null;
        beanClass = null;
        init = true;
        bean = false;
    }
    private boolean bean = false;
    public boolean isBean() {
        return bean;
    }
    public String getBeanName() {
        return beanName;
    }
    public Class<?> getBeanClass() {
        return beanClass;
    }
    public boolean isInit() {
        return init;
    }
}