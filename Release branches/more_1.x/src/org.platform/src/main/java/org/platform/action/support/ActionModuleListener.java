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
package org.platform.action.support;
import org.platform.binder.ApiBinder;
import org.platform.context.AppContext;
import org.platform.context.ContextListener;
import org.platform.context.InitListener;
/**
 * Action服务启动类，用于装载action。
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@InitListener(displayName = "ActionModuleListener", description = "org.platform.action软件包功能支持。", startIndex = 0)
public class ActionModuleListener implements ContextListener {
    @Override
    public void initialize(ApiBinder binder) {
        // TODO Auto-generated method stub
    }
    @Override
    public void initialized(AppContext appContext) {
        // TODO Auto-generated method stub
    }
    @Override
    public void destroy(AppContext appContext) {
        // TODO Auto-generated method stub
    }
}