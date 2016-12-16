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
package net.test.hasor.core._07_binder;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class BinderTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    // - 类扫描
    @Test
    public void binderTest() {
        System.out.println("--->>binderTest<<--");
        AppContext appContext = Hasor.createAppContext("binder_exter.xml", new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                if (apiBinder instanceof TestBinder) {
                    ((TestBinder) apiBinder).hello();
                } else {
                    assert apiBinder instanceof TestBinder;
                }
            }
        });
        //
        logger.debug("---------------------------------------------");
        String instance = appContext.getInstance(String.class);
        logger.debug(instance);
    }
}