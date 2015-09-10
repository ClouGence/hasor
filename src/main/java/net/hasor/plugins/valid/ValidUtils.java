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
public class ValidUtils {
    private static AtomicBoolean    initValid   = new AtomicBoolean(false);
    private static Logger           logger      = LoggerFactory.getLogger(ValidUtils.class);
    private static ValidInterceptor interceptor = null;
    //
    //
    public static class ValidationModule implements Module {
        public void loadModule(ApiBinder apiBinder) throws Throwable {
            ValidInterceptor validInterceptor = new ValidInterceptor();
            validInterceptor = Hasor.autoAware(apiBinder.getEnvironment(), validInterceptor);
            apiBinder.bindInterceptor(AopMatchers.anyClass(), new ValidMatcher(), validInterceptor);
            interceptor = validInterceptor;
        }
    };
    //
    public static ValidData doValid(String validName, Object paramObj) {
        return interceptor.doValid(validName, paramObj);
    }
    //
    public static void installValid(ApiBinder apiBinder, String value, Class<Validation> validType) throws Throwable {
        if (initValid.compareAndSet(false, true)) {
            apiBinder.installModule(new ValidationModule());
            logger.info("first installValid , ValidUtils to install.");
        }
        apiBinder.bindType(Validation.class).nameWith(value).to(validType);
        logger.info("installValid name is {}, class = {}.", value, validType);
    }
}