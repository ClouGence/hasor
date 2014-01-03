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
package net.test.simple.context;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.core.context.AnnoModule;
/**
 * 
 * @version : 2013-9-14
 * @author 赵永春 (zyc@byshell.org)
 */
@AnnoModule
public class AnnoTestMod_1 implements Module {
    public void init(ApiBinder apiBinder) {
        System.out.println("AnnoTestMod_1");
        /*注册一个Integet对象，名称为theTime*/
        apiBinder.newBean("theTime").bindType(Integer.class).toInstance(456);
    }
    public void start(AppContext appContext) {
        System.out.println("start->AnnoTestMod_1");
    }
    public void stop(AppContext appContext) {
        // TODO Auto-generated method stub
    }
}