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
package net.test.project.common.global.services;
import net.hasor.core.Settings;
import net.hasor.plugins.bean.Bean;
import com.google.inject.Inject;
/**
 * 
 * @version : 2013-12-23
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
@Bean("cfg")
public class SettingsServices {
    @Inject
    private Settings settings;
    public String string(String strVal) {
        return this.settings.getString(strVal);
    }
}