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
package org.platform.runtime.startup;
import org.platform.api.context.Config;
import org.platform.api.context.InitContext;
import org.platform.runtime.context.AppContextFactory;
import org.platform.runtime.execycle.ExecuteCycle;
import org.platform.runtime.manager.AppManager;
import com.google.inject.Injector;
/**
 * 
 * @version : 2013-4-8
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class PlatformBuild {
    private Config      config              = null;
    private InitContext platformInitContext = null;
    //
    //
    //
    // 
    public void buildPlatform(InitContext initContext, Injector guice) {
        // TODO Auto-generated method stub
    }
    //
    //
    //
    //    
    //
    //
    // 
    //  
    public AppContextFactory getAppContextFactory() {
        // TODO Auto-generated method stub
        return null;a
    }
    public ExecuteCycle getExecuteCycle() {
        // TODO Auto-generated method stub
        return null;a
    }
    public AppManager buildManager() {
        // TODO Auto-generated method stub
        return null;a
    }
}