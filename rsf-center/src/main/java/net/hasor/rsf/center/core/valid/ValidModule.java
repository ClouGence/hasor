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
package net.hasor.rsf.center.core.valid;
import java.util.Set;
import net.hasor.plugins.valid.ValidUtils;
import net.hasor.plugins.valid.Validation;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
/**
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ValidModule extends WebModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        Set<Class<?>> validSet = apiBinder.getEnvironment().findClass(ValidDefine.class);
        for (Class<?> validType : validSet) {
            if (Validation.class.isAssignableFrom(validType)) {
                ValidDefine validDefine = validType.getAnnotation(ValidDefine.class);
                ValidUtils.installValid(apiBinder, validDefine.value(), (Class<Validation>) validType);
            }
        }
    }
}