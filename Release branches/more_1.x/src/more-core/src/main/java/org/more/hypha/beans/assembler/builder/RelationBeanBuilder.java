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
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.ApplicationContext;
import org.more.hypha.NoDefineBeanException;
import org.more.hypha.beans.assembler.MetaDataUtil;
import org.more.hypha.commons.logic.AbstractBeanBuilder;
import org.more.hypha.define.RelationBeanDefine;
/**
 * 引用类型bean。
 * @version 2011-2-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class RelationBeanBuilder extends AbstractBeanBuilder<RelationBeanDefine> {
    private static Log log = LogFactory.getLog(RelationBeanBuilder.class);
    /*------------------------------------------------------------------------------*/
    private String getName(RelationBeanDefine define) {
        String ref = define.getRef();
        String refPackage = define.getRefPackage();
        ApplicationContext app = this.getApplicationContext();
        if (app.containsBean(ref) == false) {
            ref = refPackage + "." + ref;
            if (app.containsBean(ref) == false) {
                log.error("ref bean {%0} is not exist", ref);
                throw new NoDefineBeanException("ref bean " + ref + " is not exist");
            }
        }
        return ref;
    }
    public Class<?> loadType(RelationBeanDefine define, Object[] params) throws Throwable {
        Class<?> fungiClass = MetaDataUtil.getTypeForFungi(define, log);
        //1.测试缓存。
        if (fungiClass != null)
            return fungiClass;
        //2.确定bean。
        String ref = getName(define);
        //3.获取ref
        Class<?> bType = this.getApplicationContext().getBeanType(ref, params);
        MetaDataUtil.putTypeToFungi(define, bType, log);
        log.debug("ref bean type is {%0}.", bType);
        return bType;
    }
    public Object loadBean(RelationBeanDefine define, Object[] params) throws Throwable {
        //1.确定bean名称。
        String ref = getName(define);
        //2.获取bean。
        Object obj = this.getApplicationContext().getBean(ref, params);
        log.debug("ref bean value = {%0}", obj);
        return obj;
    }
};