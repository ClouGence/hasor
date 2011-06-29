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
package org.more.hypha.aop.xml;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.Event.Sequence;
import org.more.hypha.EventListener;
import org.more.hypha.aop.AopInfoConfig;
import org.more.hypha.aop.assembler.AopInfoConfig_Impl;
import org.more.hypha.context.xml.XmlDefineResource;
import org.more.hypha.context.xml.XmlLoadedEvent;
import org.more.util.StringUtil;
import org.more.util.attribute.IAttribute;
/**
 * 该类是当{@link XmlDefineResource}触发{@link XmlLoadedEvent}类型事件时处理anno:apply标签配置的应用Package级别操作。
 * @version 2010-10-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class Listener_ToPackageApply implements EventListener<XmlLoadedEvent> {
    private String config = null, toPackageExp = "*";
    //----------------------------------------------
    /**创建{@link Listener_ToPackageApply}对象。*/
    public Listener_ToPackageApply(String config, String toPackageExp) {
        this.config = config;
        this.toPackageExp = toPackageExp;
    };
    /**执行Package应用。*/
    public void onEvent(final XmlLoadedEvent event, final Sequence sequence) {
        XmlDefineResource config = event.toParams(sequence).xmlDefineResource;
        IAttribute flash = config.getFlash();
        AopInfoConfig aopPlugin = (AopInfoConfig) flash.getAttribute(AopInfoConfig_Impl.ServiceName);
        for (String defineName : config.getBeanDefinitionIDs()) {
            AbstractBeanDefine define = config.getBeanDefine(defineName);
            if (StringUtil.matchWild(this.toPackageExp, define.getID()) == true)
                aopPlugin.setAop(define, this.config);
        }
    };
};