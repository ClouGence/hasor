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
package org.more.hypha.context.array;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.DefineResource;
import org.more.hypha.Event;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * 开始初始化过程事件，该事件是{@link DefineResource}。收到一个新{@link AbstractBeanDefine}定义添加时引发。
 * @version 2010-10-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class AddDefineEvent extends Event {
    private static Log log = LogFactory.getLog(AddDefineEvent.class);
    public class AddDefineEvent_Params extends Event.Params {
        public DefineResource     defineResource = null;
        public AbstractBeanDefine define         = null;
    };
    public AddDefineEvent_Params toParams(Sequence eventSequence) {
        Object[] params = eventSequence.getParams();
        log.debug("Sequence to Params ,params = {%0}", params);
        AddDefineEvent_Params p = new AddDefineEvent_Params();
        p.defineResource = (XmlDefineResource) params[0];
        p.define = (AbstractBeanDefine) params[1];
        return p;
    }
};