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
package org.hasor.mvc.controller.plugins.result.support;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hasor.context.HasorEventListener;
import org.hasor.mvc.controller.support.ActionInvoke;
/**
 * 
 * @version : 2013-8-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Singleton
class Caller implements HasorEventListener {
    @Inject
    private ResultProcessManager resultProcessManager;
    @Override
    public void onEvent(String event, Object[] params) {
        ActionInvoke invoke = (ActionInvoke) params[0];
        Object[] invokeParams = (Object[]) params[1];
        Object returnData = params[2];
        //
        System.out.println();s
    }
}