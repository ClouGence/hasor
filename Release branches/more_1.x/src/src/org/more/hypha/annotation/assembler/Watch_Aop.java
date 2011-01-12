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
package org.more.hypha.annotation.assembler;
import org.more.LostException;
import org.more.hypha.DefineResource;
import org.more.hypha.annotation.AnnoResourceExpand;
import org.more.hypha.annotation.Aop;
import org.more.hypha.annotation.AopInformed;
import org.more.hypha.annotation.Bean;
import org.more.hypha.annotation.KeepWatchParser;
import org.more.hypha.aop.AopDefineExpand_Impl;
import org.more.hypha.aop.AopResourceExpand;
import org.more.hypha.aop.define.AopConfigDefine;
import org.more.hypha.aop.define.AopDefineInformed;
import org.more.hypha.aop.define.AopPointcutDefine;
import org.more.hypha.aop.define.PointcutType;
import org.more.hypha.beans.AbstractBeanDefine;
/**
 * 该bean用于解析aop的注解配置。
 * @version 2010-10-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class Watch_Aop implements KeepWatchParser {
    public void process(Class<?> beanType, DefineResource resource, AnnoResourceExpand plugin) {
        Bean bean = beanType.getAnnotation(Bean.class);
        AopResourceExpand aopPlugin = (AopResourceExpand) resource.getPlugin(AopResourceExpand.AopDefineResourcePluginName);
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
            AopDefineExpand_Impl beanPlugin = new AopDefineExpand_Impl(define, aopConfig);
            define.setPlugin(AopDefineExpand_Impl.AopPluginName, beanPlugin);
            return;
        }
        //3.解析aop注解
        aopConfig = new AopConfigDefine();
        AopDefineExpand_Impl beanPlugin = new AopDefineExpand_Impl(define, aopConfig);
        define.setPlugin(AopDefineExpand_Impl.AopPluginName, beanPlugin);
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
    }
}