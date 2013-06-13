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
package org.platform.security.support;
import java.util.List;
import org.more.util.StringUtils;
import org.platform.context.AppContext;
import org.platform.security.SecurityDispatcher;
/**
 * 
 * @version : 2013-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
class InternalDispatcherManager {
    private SecuritySettings securitySettings = null;
    /**根据uri获取可用于跳转工具类。*/
    public SecurityDispatcher getDispatcher(String requestPath) {
        if (StringUtils.isBlank(requestPath) == true)
            return null;
        List<SecurityDispatcher> dispatcherList = securitySettings.getDispatcherForwardList();
        for (SecurityDispatcher dispatcher : dispatcherList)
            if (requestPath.startsWith(dispatcher.getContentPath()) == true)
                return dispatcher;
        return null;
    }
    public void initManager(AppContext appContext) {
        this.securitySettings = appContext.getInstance(SecuritySettings.class);
    }
    public void destroyManager(AppContext appContext) {};
}