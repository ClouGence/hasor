/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.core.context.init;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import net.hasor.core.InitContext;
import net.hasor.core.Settings;
import net.hasor.core.setting.MappingInitContextSettings;
/**
 * {@link InitContext}接口实现类。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public class MappingInitContext extends DefaultInitContext {
    public MappingInitContext() throws IOException {
        super();
    }
    public MappingInitContext(URI mainSettings) throws IOException {
        super(mainSettings);
    }
    public MappingInitContext(URI mainSettings, Object context) throws IOException {
        super(mainSettings, context);
    }
    public MappingInitContext(File mainSettings) throws IOException {
        super((mainSettings == null) ? null : mainSettings.toURI());
    }
    public MappingInitContext(File mainSettings, Object context) throws IOException {
        super((mainSettings == null) ? null : mainSettings.toURI(), context);
    }
    //---------------------------------------------------------------------------------Basic Method
    protected Settings createSettings(URI settingURI) throws IOException {
        MappingInitContextSettings settings = null;
        if (settingURI == null)
            settings = new MappingInitContextSettings();
        else
            settings = new MappingInitContextSettings(settingURI);
        this.settingURI = settings.getSettingURI();
        return settings;
    }
}