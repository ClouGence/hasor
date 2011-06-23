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
package org.more.hypha.el;
import java.util.List;
import org.more.hypha.ELContext;
import org.more.hypha.ELObject;
import org.more.hypha.Event.Sequence;
import org.more.hypha.EventListener;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.hypha.context.InitEvent;
import org.more.hypha.el.xml.EL_NS;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * el的初始化EL对象。
 * @version : 2011-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
class OnInit implements EventListener<InitEvent> {
    private static ILog log = LogFactory.getLog(OnInit.class);
    public void onEvent(InitEvent event, Sequence sequence) throws Throwable {
        AbstractApplicationContext context = (AbstractApplicationContext) event.toParams(sequence).applicationContext;
        ClassLoader loader = context.getBeanClassLoader();
        ELContext elContext = context.getELContext();
        //3.注册元信息解析器
        List<B_EL> elList = (List<B_EL>) context.getFlash().getAttribute(EL_NS.ELConfigList);
        if (elList != null)
            for (B_EL el : elList) {
                Class<?> elClass = loader.loadClass(el.getClassName());
                ELObject elObject = (ELObject) elClass.newInstance();
                elObject.init(context, context.getFlash());
                elContext.addELObject(el.getName(), elObject);
            }
        log.info("hypha.el init OK!");
    };
}