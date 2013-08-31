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
package org.test.dep.before;
import org.hasor.context.ApiBinder;
import org.hasor.context.module.AbstractHasorModule;
/**
 * 
 * @version : 2013-7-27
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
public class Mode1 extends AbstractHasorModule {
    public void init(ApiBinder apiBinder) {
        throw new RuntimeException("this is my Error");
        //System.out.println("Mode1  init!");
    }
}