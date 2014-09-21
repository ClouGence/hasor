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
package net.test.simple.core._12_ioc;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import org.junit.Test;
/**
 * 
 * @version : 2014年9月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class IocTest {
    @Test
    public void iocTest() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>iocTest<<--");
        AppContext appContext = Hasor.createAppContext(new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                apiBinder.bindType(UserBean.class);
                apiBinder.bindType(UserTypeBean.class);
            }
        });
        //
        UserBean userBean = appContext.getInstance(UserBean.class);
        //
        System.out.println(userBean.getUserType().getTypeID());
        Thread.sleep(1000);
    }
}