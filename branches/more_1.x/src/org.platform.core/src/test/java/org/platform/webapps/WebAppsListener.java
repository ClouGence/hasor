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
package org.platform.webapps;
import org.moreframework.binder.ApiBinder;
import org.moreframework.context.AppContext;
import org.moreframework.context.PlatformListener;
import org.moreframework.startup.PlatformExt;
import org.platform.webapps.business.scene1.web.Scene1_HttpServlet;
import org.platform.webapps.error.define.GoException;
import org.platform.webapps.error.process.GoException_Process;
/**
 * 
 * @version : 2013-4-29
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@PlatformExt(displayName = "WebAppsListener", description = "WebAppsListener≤‚ ‘°£", startIndex = 10)
public class WebAppsListener implements PlatformListener {
    @Override
    public void initialize(ApiBinder binder) {
        binder.serve("/business/scene1.do").with(Scene1_HttpServlet.class);
        binder.error(GoException.class).bind(GoException_Process.class);
    }
    @Override
    public void initialized(AppContext appContext) {}
    @Override
    public void destroy(AppContext appContext) {}
}