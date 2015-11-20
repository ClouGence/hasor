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
package net.hasor.plugins.valid;
import java.util.concurrent.atomic.AtomicBoolean;
import net.hasor.core.ApiBinder;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.binder.aop.matcher.AopMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class ValidModule implements Module {
    private Logger        logger    = LoggerFactory.getLogger(getClass());
    private AtomicBoolean initValid = new AtomicBoolean(false);
    //
    public final void loadModule(ApiBinder apiBinder) throws Throwable {
        if (initValid.compareAndSet(false, true)) {
            final ValidInterceptor valid = Hasor.autoAware(apiBinder.getEnvironment(), new ValidInterceptor());
            apiBinder.bindInterceptor(AopMatchers.anyClass(), new ValidMatcher(), valid);
            //
            final ValidApi api = new ValidApi() {
                public ValidData doValid(String validName, Object paramObj) {
                    return valid.doValid(validName, paramObj);
                }
            };
            apiBinder.bindType(ValidApi.class).toInstance(api);
            logger.info("first installValid , ValidUtils to install.");
        }
        //
    }
    //
    public void loadValid(ValidApiBinder apiBinder) throws Throwable {
        //
    }
}