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
package org.test.more.beans.define;
import org.junit.Test;
import org.more.hypha.ApplicationContext;
import org.more.hypha.Event;
import org.more.hypha.EventListener;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.beans.AbstractPropertyDefine;
import org.more.hypha.context.XmlDefineResource;
import org.more.hypha.event.AddBeanDefineEvent;
import org.more.hypha.event.AddPluginEvent;
import org.more.hypha.event.ClearDefineEvent;
import org.more.hypha.event.Config_LoadResourceEvent;
import org.more.hypha.event.Config_LoadedXmlEvent;
import org.more.hypha.event.Config_LoadingXmlEvent;
import org.more.hypha.event.ReloadDefineEvent;
/**
 * ≤‚ ‘¡Ànamespace∞¸,define∞¸
 * @version 2010-9-21
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class XmlTest {
    @Test
    public void test() throws Throwable {
        EventListener listener = new EventListener() {
            public void onEvent(Event event) {
                System.out.println("Event:" + event.getClass());
                if (event instanceof AddBeanDefineEvent) {
                    AddBeanDefineEvent ade = (AddBeanDefineEvent) event;
                    AbstractBeanDefine define = ade.getDefine();
                    System.out.println(define.getID());
                    //
                } else if (event instanceof Config_LoadResourceEvent) {
                    Config_LoadResourceEvent lre = (Config_LoadResourceEvent) event;
                    System.out.println(lre.getResource());
                }
            }
        };
        System.out.println("start...");
        XmlDefineResource config = XmlDefineResource.newInstanceByNew();
        config.getEventManager().addEventListener(AddBeanDefineEvent.class, listener);
        config.getEventManager().addEventListener(AddPluginEvent.class, listener);
        config.getEventManager().addEventListener(ClearDefineEvent.class, listener);
        config.getEventManager().addEventListener(Config_LoadedXmlEvent.class, listener);
        config.getEventManager().addEventListener(Config_LoadingXmlEvent.class, listener);
        config.getEventManager().addEventListener(ReloadDefineEvent.class, listener);
        config.getEventManager().addEventListener(Config_LoadResourceEvent.class, listener);
        //
        config.addSource("/org/test/more/beans/define/property-test-config.xml");
        config.addSource("/org/test/more/beans/define/collection-test-config.xml");
        config.addSource("/org/test/more/beans/define/beans-test-config.xml");
        config.addSource("/org/test/more/beans/define/aop-test-config.xml");
        config.loadDefine();
        //
        ApplicationContext app = config.buildApp(null);
        //DefineResource con = config.build(null, null);
        //
        for (String bean : config.getBeanDefineNames()) {
            if (bean.equals("org.more.rb_1") == true)
                System.out.println();
            System.out.println("--getBean:" + bean);
            AbstractBeanDefine define = config.getBeanDefine(bean);
            System.out.println(define);
            for (AbstractPropertyDefine pd : define.getPropertys())
                System.out.println(pd.getMetaData());
        }
        System.out.println("end!");
    }
}