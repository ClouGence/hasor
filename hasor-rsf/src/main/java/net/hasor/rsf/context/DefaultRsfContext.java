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
package net.hasor.rsf.context;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.Settings;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.remoting.binder.AbstractBindCenter;
import net.hasor.rsf.remoting.binder.DefaultBindCenter;
/**
 * 
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultRsfContext extends AbstractRsfContext {
    /**设置主配置文件*/
    public DefaultRsfContext() throws IOException, URISyntaxException {
        super(createSettings());
    }
    /***/
    public DefaultRsfContext(Settings settings) {
        super(settings);
    }
    /***/
    public AbstractBindCenter createBindCenter() {
        return new DefaultBindCenter(this);
    }
    private static Settings createSettings() throws IOException, URISyntaxException {
        Settings settings = new StandardContextSettings();
        settings.refresh();
        return settings;
    }
}