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
package org.hasor.test.dependency.test1;
import org.hasor.context.ApiBinder;
import org.hasor.context.ModuleSettings;
import org.hasor.context.anno.Module;
import org.hasor.context.reactor.AbstractHasorModule;
/**
 * 
 * @version : 2013-7-27
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Module()
public class Mode2 extends AbstractHasorModule {
    @Override
    public void configuration(ModuleSettings info) {
        info.beforeMe(Mode4.class);
    }
    @Override
    public void init(ApiBinder apiBinder) {
        System.out.println("Mode2  init!");
    }
}