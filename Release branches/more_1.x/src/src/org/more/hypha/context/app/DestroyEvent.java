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
package org.more.hypha.context.app;
import org.more.hypha.ApplicationContext;
import org.more.hypha.Event;
/**
 * {@link ApplicationContext}遇到的卸载调用而引发事件。
 * @version 2011-2-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class DestroyEvent extends Event {
    public class Params extends Event.Params {
        public ApplicationContext applicationContext = null;
    };
    public Params toParams(Sequence eventSequence) {
        Object[] params = eventSequence.getParams();
        Params p = new Params();
        p.applicationContext = (ApplicationContext) params[0];
        return p;
    }
};