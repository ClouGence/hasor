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
import net.hasor.core.Module;
/**
 * 模块
 * @version : 2015年11月9日
 * @author 赵永春(zyc@hasor.net)
 */
public class SimpleModule implements Module {
    public static final String STR = "say form Mod_2.";
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        apiBinder.bindType(String.class).uniqueName().toInstance(STR);
    }
}