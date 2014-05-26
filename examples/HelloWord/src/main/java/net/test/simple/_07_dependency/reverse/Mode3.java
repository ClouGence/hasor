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
package net.test.simple._07_dependency.reverse;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.quick.anno.AnnoModule;
/**
 * 依赖反制演示，依赖关系：
 * Mode3
 *    Mode1
 *    Mode2
 * @version : 2013-7-27
 * @author 赵永春 (zyc@hasor.net)
 */
@AnnoModule()
public class Mode3 implements Module {
    public void init(ApiBinder apiBinder) {
        //在不修改 Module1 和 Module2 代码的前提下让它们依赖 Moule 3。
        apiBinder.configModule().reverse(Mode1.class);
        apiBinder.configModule().reverse(Mode2.class);
    }
    public void start(AppContext appContext) {
        System.out.println("Mode3 start!");
    }
}
