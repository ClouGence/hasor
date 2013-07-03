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
package org.demo.webapps;
import org.hasor.binder.ApiBinder;
import org.hasor.context.AppContext;
import org.hasor.context.PlatformListener;
import org.hasor.startup.PlatformExt;
/**
 * 
 * @version : 2013-4-29
 * @author ������ (zyc@byshell.org)
 */
@PlatformExt(displayName = "WebAppsListener", description = "WebAppsListener���ԡ�", startIndex = 10)
public class WebAppsListener implements PlatformListener {
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