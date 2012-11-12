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
package org.more.hypha.aop.assembler;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.ApplicationContext;
import org.more.hypha.aop.AopService;
import org.more.hypha.commons.logic.CreateBeanPoint;
import org.more.hypha.define.AopConfigDefine;
import org.more.hypha.point.PointChain;
/**
 * 
 * @version : 2011-6-28
 * @author 赵永春 (zyc@byshell.org)
 */
public class AopCreateBeanPoint implements CreateBeanPoint {
    private static Log log = LogFactory.getLog(AopCreateBeanPoint.class);
    public Object doFilter(ApplicationContext applicationContext, Object[] params, PointChain chain) throws Throwable {
        //合法性判断
        if (params == null || params.length == 0)
            return chain.doChain(applicationContext, params);
        Object obj = params[0];
        if (obj != null && obj instanceof AbstractBeanDefine == true) {} else
            return chain.doChain(applicationContext, params);
        //处理Aop
        AbstractBeanDefine define = (AbstractBeanDefine) params[0];
        AopService_Impl aopConfig = (AopService_Impl) applicationContext.getService(AopService.class);
        if (aopConfig == null) {
            log.warning("app {%1} services not include AopServices!", applicationContext.getID());
            return chain.doChain(applicationContext, params);
        }
        AopConfigDefine aopDefine = aopConfig.getAopDefine(define);
        if (aopDefine == null)
            return chain.doChain(applicationContext, params);
        Object beanObject = chain.doChain(applicationContext, params);
        /*---------------------------------------*/
        //                包含AOP
        /*---------------------------------------*/
        AopBuilder aopBuilder = aopConfig.getAopBuilder();
        Object aopBean = aopBuilder.builderBean(beanObject, aopDefine, define);
        if (aopBean != null)
            return aopBean;
        return beanObject;
    }
}