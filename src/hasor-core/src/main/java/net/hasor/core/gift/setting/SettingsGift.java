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
package net.hasor.core.gift.setting;
import static net.hasor.core.context.AbstractAppContext.ContextEvent_Start;
import java.util.Set;
import net.hasor.Hasor;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.EventListener;
import net.hasor.core.EventManager;
import net.hasor.core.SettingsListener;
import net.hasor.core.gift.Gift;
import net.hasor.core.gift.GiftFace;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
@Gift
public class SettingsGift implements GiftFace {
    public void loadGift(ApiBinder apiBinder) {
        this.loadAnnoSettings(apiBinder);
        //
        final Environment env = apiBinder.getEnvironment();
        EventManager eventManager = env.getEventManager();
        eventManager.pushEventListener(ContextEvent_Start, new EventListener() {
            public void onEvent(String event, Object[] params) {
                AppContext appContext = (AppContext) params[0];
                Provider<SettingsListener>[] settingProvider = appContext.getProviderByBindingType(SettingsListener.class);
                if (settingProvider == null)
                    return;
                for (Provider<SettingsListener> provider : settingProvider) {
                    SettingsListener target = provider.get();
                    target.onLoadConfig(appContext.getSettings());
                    env.addSettingsListener(target);
                    Hasor.info("%s SettingsListener created.", target);
                }
            }
        });
    }
    /**装载注解形式的SettingsListener*/
    private void loadAnnoSettings(ApiBinder apiBinder) {
        final Environment env = apiBinder.getEnvironment();
        Set<Class<?>> settingSet = env.getClassSet(Settings.class);
        if (settingSet == null || settingSet.isEmpty())
            return;
        for (Class<?> settingClass : settingSet) {
            if (SettingsListener.class.isAssignableFrom(settingClass) == false) {
                Hasor.warning("not implemented SettingsListener :%s", settingClass);
                continue;
            }
            apiBinder.bindingType(SettingsListener.class, (Class<SettingsListener>) settingClass).asEagerSingleton();
            Hasor.info("%s bind SettingsListener.", settingClass);
        }
    }
}