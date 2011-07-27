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
/**
 * SubmitContext接口的实现类。ImplSubmitContext的createActionStack方法可以改变Action参数的具体类型。
 * @version 2009-12-1
 * @author 赵永春 (zyc@byshell.org)
 */
public class SubmitContextImpl extends AbstractSubmitContextImpl {
    /**  */
    private static final long serialVersionUID = 5057959033615801491L;
    public SubmitContextImpl(ActionContext actionContext) {
        this.setActionContext(actionContext);
    };
    public boolean isWebContext() {
        return false;
    };
    protected ActionStack createStack(String actionName, String actionMethod, ActionStack parent, Session session) {
        return new ActionStack(actionName, actionMethod, parent, session, this);
    };
};