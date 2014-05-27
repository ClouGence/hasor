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
package net.test.simple._09_kernel.mods;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.quick.anno.AnnoModule;
/**
 * 模块3，依赖模块2，模块2，依赖模块1
 * @version : 2013-9-14
 * @author 赵永春 (zyc@byshell.org)
 */
@AnnoModule
public class Mod_3 implements Module {
    public void init(ApiBinder apiBinder) {
        /*弱依赖，即使依赖的模块没有正常启动，模块3依然启动。*/
        apiBinder.configModule().weak(Mod_2.class);
    }
    public void start(AppContext appContext) {
        System.out.println("start->Mod_3");
    }
}
