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
package org.more.submit;
import org.more.InvokeException;
/**
 * 过滤器链的一个环节。
 * Date : 2009-6-25
 * @author 赵永春
 */
public class ActionFilterChain extends PropxyAction {
    private ActionFilter      filter    = null; //过滤器链中的当前过滤器。
    private ActionFilterChain nextChain = null; //当前过滤器中拥有的下一个过滤器链节点。
    //=================================================
    public ActionFilterChain(PropxyAction delegate, ActionFilter filter, ActionFilterChain nextChain) {
        this.target = delegate;//代理对象有可能层层代理。因此这个对象有可能还是一个PropxyAction
        this.filter = filter;
        this.nextChain = nextChain;
    }
    //=================================================
    public Object execute(String methodName, ActionMethodEvent event) throws InvokeException {
        if (this.nextChain == null) {
            PropxyAction pa = (PropxyAction) this.target;
            return pa.execute(methodName, event);
        } else
            return this.filter.doActionFilter(methodName, event, this.nextChain);
    }
}
