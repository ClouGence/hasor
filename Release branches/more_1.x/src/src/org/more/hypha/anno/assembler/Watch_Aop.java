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
package org.more.hypha.anno.assembler;
import org.more.core.error.LostException;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.anno.AnnoServices;
import org.more.hypha.anno.KeepWatchParser;
import org.more.hypha.anno.define.Aop;
import org.more.hypha.anno.define.AopInformed;
import org.more.hypha.anno.define.Bean;
import org.more.hypha.aop.AopInfoConfig;
import org.more.hypha.aop.assembler.AopInfoConfig_Impl;
import org.more.hypha.aop.define.AopConfigDefine;
import org.more.hypha.aop.define.AopDefineInformed;
import org.more.hypha.aop.define.AopPointcutDefine;
import org.more.hypha.aop.define.PointcutType;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * 该bean用于解析aop的注解配置。
 * @version 2010-10-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class Watch_Aop implements KeepWatchParser {
    public void process(Class<?> beanType, XmlDefineResource resource, AnnoServices plugin) {
        Bean bean = beanType.getAnnotation(Bean.class);
        if (bean == null)
            return;
        AopInfoConfig aopPlugin = (AopInfoConfig) resource.getFlash().getAttribute(AopInfoConfig_Impl.ServiceName);
        // ID
        String id = bean.id();
        if (id.equals("") == true) {
            StringBuffer idb = new StringBuffer();
            String logicPackage = bean.logicPackage();
            if (logicPackage.equals("") == true)
                logicPackage = beanType.getPackage().getName();
            idb.append(logicPackage);
            idb.append(".");
            String name = bean.name();
            if (name.equals("") == true)
                name = beanType.getSimpleName();
            idb.append(name);
            id = idb.toString();
        }
        //1.获取aop注解
        AbstractBeanDefine define = resource.getBeanDefine(id);
        Aop aop = beanType.getAnnotation(Aop.class);
        if (aop == null)
            return;
        //2.检查useConfig属性。
        AopConfigDefine aopConfig = null;
        String var = aop.useConfig();
        if (var.equals("") == false) {
            aopConfig = aopPlugin.getAopDefine(var);
            if (aopConfig == null)
                throw new LostException("找不到名称为[" + var + "]的Aop配置。");
            aopPlugin.setAop(define, aopConfig);
            return;
        }
        //3.解析aop注解
        aopConfig = new AopConfigDefine();
        aopConfig.setAopMode(aop.mode());
        //defaultPointcut
        AopPointcutDefine aoppoint = new AopPointcutDefine();
        aoppoint.setExpression(aop.defaultPointcut());
        aopConfig.setDefaultPointcutDefine(aoppoint);
        //informeds
        AopInformed[] informeds = aop.informeds();
        for (AopInformed informed : informeds) {
            String refBean = informed.refBean();
            String pointcut = informed.pointcut();
            PointcutType pointType = informed.type();
            //切入点定义
            AopPointcutDefine aoppoint_item = new AopPointcutDefine();
            aoppoint.setExpression(pointcut);
            //监听器定义
            AopDefineInformed informedDefine = new AopDefineInformed();
            informedDefine.setRefBean(refBean);
            informedDefine.setPointcutType(pointType);
            informedDefine.setRefPointcut(aoppoint_item);
            //
            aopConfig.addInformed(informedDefine);
        }
        //4.注册
        aopPlugin.setAop(define, aopConfig);
    }
}