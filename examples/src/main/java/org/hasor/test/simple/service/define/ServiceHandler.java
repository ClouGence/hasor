/*
 * Copyright 2008-2009 the original ÕÔÓÀ´º(zyc@hasor.net).
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
package org.hasor.test.simple.service.define;
import net.hasor.core.register.ServicesRegisterHandler;
/**
 * 
 * @version : 2013-10-29
 * @author ÕÔÓÀ´º(zyc@hasor.net)
 */
public class ServiceHandler implements ServicesRegisterHandler<AbstractService> {
    public void registerService(AbstractService serviceBean) {
        //×¢²á·þÎñ
        System.out.println("add Service :" + serviceBean.say());
    }
    public void unRegisterService(AbstractService serviceBean) {
        //½â³ý×¢²á
        System.out.println("remove Service :" + serviceBean.say());
    }
}