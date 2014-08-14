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
package net.test.simple.core._02_bind;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import org.junit.Test;
/**
 * 本示列演示带有名字的绑定。
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class NameBindTest {
    @Test
    public void nameBindTest() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>nameBindTest<<--");
        //1.创建一个标准的 Hasor 容器。
        AppContext appContext = Hasor.createAppContext(new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                //绑定一个接口和实现类
                apiBinder.bindType(CharSequence.class).nameWith("ModuleA").toInstance("this String form A");
                apiBinder.bindType(CharSequence.class).nameWith("ModuleB").toInstance("this String form B");
            }
        });
        //
        System.out.println();
        CharSequence modeSay = null;
        modeSay = appContext.findBindingBean("ModuleA", CharSequence.class);
        Hasor.logInfo(modeSay.toString());
        modeSay = appContext.findBindingBean("ModuleB", CharSequence.class);
        Hasor.logInfo(modeSay.toString());
        //
        List<CharSequence> says = appContext.findBindingBean(CharSequence.class);//查找绑定
        Hasor.logInfo("say %s.", says);
    }
}