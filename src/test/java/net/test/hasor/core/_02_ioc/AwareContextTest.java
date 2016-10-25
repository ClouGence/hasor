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
package net.test.hasor.core._02_ioc;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.test.hasor.core._02_ioc.aware.AwareBean;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 本示列演示如何让 Hasor在启动时自动将AppContext注入到需要的地方。
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class AwareContextTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Test
    public void awareContextTest() {
        System.out.println("--->>awareContextTest<<--");
        AppContext appContext = Hasor.createAppContext(new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                //由于init过程中无法取得 appContext对象，因此让Hasor在适当的时机将自身注入进去。
                apiBinder.bindType(AwareBean.class);
                apiBinder.bindType(String.class).nameWith("say").toInstance("Say Hello.");
            }
        });
        logger.debug("---------------------------------------------");
        //
        AwareBean awareBean = appContext.getInstance(AwareBean.class);
        assert !awareBean.called;
        awareBean.foo();
        assert awareBean.called;
    }
}