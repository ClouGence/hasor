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
import org.platform.binder.ApiBinder;
import org.platform.context.AbstractModuleListener;
import org.platform.context.InitListener;
import org.platform.webapps.business.scene2.service.Scene2_ServiceBean;
/**
 * 
 * @version : 2013-4-29
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@InitListener(displayName = "WebAppsListener", description = "WebAppsListener≤‚ ‘°£", startIndex = 10)
public class WebAppsListener extends AbstractModuleListener {
    @Override
    public void initialize(ApiBinder binder) {
        binder.newBean("Scene2_ServiceBean").bindType(Scene2_ServiceBean.class).asEagerSingleton();
    }
}