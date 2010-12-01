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
package org.more.hypha.aop.support;
import org.more.hypha.DefineResource;
import org.more.hypha.Event;
import org.more.hypha.EventListener;
import org.more.hypha.aop.AopBeanDefinePlugin;
import org.more.hypha.aop.AopDefineResourcePlugin;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.event.Config_LoadedXmlEvent;
import org.more.util.StringUtil;
/**
 * 该类是当{@link DefineResourceImpl}触发{@link Config_EndBuildEvent}类型事件时处理anno:apply标签配置的应用Package级别操作。
 * @version 2010-10-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class Listener_ToPackageApply implements EventListener {
    private String config = null, toPackageExp = "*";
    //----------------------------------------------
    /**创建{@link Listener_ToPackageApply}对象。*/
    public Listener_ToPackageApply(String config, String toPackageExp) {
        this.config = config;
        this.toPackageExp = toPackageExp;
    }
    /**执行Package应用。*/
    public void onEvent(Event event) {
        Config_LoadedXmlEvent eve = (Config_LoadedXmlEvent) event;
        DefineResource config = eve.getResource();
        AopDefineResourcePlugin aopPlugin = (AopDefineResourcePlugin) config.getPlugin(AopDefineResourcePlugin.AopDefineResourcePluginName);
        for (String defineName : config.getBeanDefineNames()) {
            AbstractBeanDefine define = config.getBeanDefine(defineName);
            if (StringUtil.matchWild(this.toPackageExp, define.getFullName()) == true)
                if (define.getPlugin(AopBeanDefinePlugin.AopPluginName) == null)
                    aopPlugin.setAop(define, this.config);
        }
    }
}