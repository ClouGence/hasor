/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.hasor.context.module;
import org.hasor.Hasor;
import org.hasor.context.ApiBinder;
import org.hasor.context.ModuleSettings;
import org.hasor.context.anno.support.AnnoSupportModule;
import com.google.inject.Module;
/**
 * 
 * @version : 2013-7-16
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
public class GuiceModulePropxy extends AbstractHasorModule {
    private Module guiceModule = null;
    //
    public GuiceModulePropxy(Module guiceModule) {
        Hasor.assertIsNotNull(guiceModule);
        this.guiceModule = guiceModule;
    }
    //
    public void configuration(ModuleSettings info) {
        info.beforeMe(AnnoSupportModule.class);
    }
    //
    public void init(ApiBinder apiBinder) {
        apiBinder.getGuiceBinder().install(this.guiceModule);
    }
}