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
package org.hasor.test.simple.service.ser1;
import java.util.ArrayList;
import java.util.List;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.core.context.AnnoModule;
import org.hasor.test.simple.service.define.AbstractService;
/**
 * 
 * @version : 2013-9-14
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@AnnoModule
public class AnnoTestMod_A implements Module {
    private List<AbstractService> serSet = new ArrayList<AbstractService>();
    //
    public void init(ApiBinder apiBinder) {
        System.out.println("AnnoTestMod_A");
    }
    public void start(AppContext appContext) {
        serSet.add(new SerA1());
        serSet.add(new SerA2());
        //
        for (AbstractService ser : serSet)
            appContext.registerService(AbstractService.class, ser);
    }
    public void stop(AppContext appContext) {
        for (AbstractService ser : serSet)
            appContext.unRegisterService(AbstractService.class, ser);
    }
}