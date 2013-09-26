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
package net.hasor.gift.guice;
import java.util.Set;
import net.hasor.Hasor;
import net.hasor.core.ApiBinder;
import net.hasor.core.gift.Gift;
import net.hasor.core.gift.GiftFace;
import com.google.inject.Module;
/**
 * 
 * @version : 2013-9-13
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Gift
public class GuiceGift implements GiftFace {
    public void loadGift(ApiBinder apiBinder) {
        Set<Class<?>> guiceModuleSet = apiBinder.getClassSet(GuiceModule.class);
        if (guiceModuleSet == null || guiceModuleSet.isEmpty())
            return;
        Hasor.info("find Module : " + Hasor.logString(guiceModuleSet));
        for (Class<?> moduleClass : guiceModuleSet) {
            if (com.google.inject.Module.class.isAssignableFrom(moduleClass) == false) {
                /*¥ÌŒÛ*/
                Hasor.warning("not implemented com.google.inject.Module :%s", moduleClass);
                continue;
            }
            try {
                apiBinder.getGuiceBinder().install((Module) moduleClass.newInstance());
                Hasor.info("install com.google.inject.Module %s.", moduleClass);
            } catch (Exception e) {
                Hasor.error("install com.google.inject.Module %s.%s", moduleClass, e);
            }
        }
    }
}