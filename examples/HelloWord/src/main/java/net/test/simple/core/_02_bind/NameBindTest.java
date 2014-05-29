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
import net.hasor.core.context.StandardAppContext;
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
        AppContext appContext = new StandardAppContext();
        appContext.addModule(new TestModule("ModuleA"));
        appContext.addModule(new TestModule("ModuleB"));
        appContext.start();//启动 Hasor 容器。
        //
        System.out.println();
        String modeSay = null;
        modeSay = appContext.findBindingBean("ModuleA", String.class);//获取ModuleA绑定的特殊字符串内容
        Hasor.logInfo(modeSay);
        modeSay = appContext.findBindingBean("ModuleB", String.class);//获取ModuleB绑定的特殊字符串内容
        Hasor.logInfo(modeSay);
        //
        List<String> says = appContext.findBindingBean(String.class);//查找绑定
        Hasor.logInfo("say %s.", says);
    }
}
class TestModule implements Module {
    private String moduleMark;
    public TestModule(String moduleMark) {
        this.moduleMark = moduleMark;
    }
    public void init(ApiBinder apiBinder) throws Throwable {
        //利用 moduleMark 为 Key，绑定一段特殊字符串内容到容器中。
        apiBinder.bindingType(String.class).nameWith(moduleMark).toInstance("this String form " + moduleMark);
    }
    public void start(AppContext appContext) throws Throwable {
        // TODO Auto-generated method stub
    }
}