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
package net.test.hasor.core._05_plugins.mods;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.LifeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 在模块中处理容器启动
 * @version : 2013-9-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class OnLifeModule implements LifeModule {
    protected           Logger logger = LoggerFactory.getLogger(getClass());
    public static final String STR    = "say form Mod_1.";
    //
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.installModule(new SimpleModule());
        apiBinder.bindType(String.class).uniqueName().toInstance(STR);
    }
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        logger.info("启动啦...");
    }
    @Override
    public void onStop(AppContext appContext) throws Throwable {
        logger.info("停止啦...");
    }
}