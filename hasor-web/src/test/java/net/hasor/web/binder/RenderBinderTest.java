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
package net.hasor.web.binder;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.test.web.actions.basic.BasicAction;
import net.hasor.test.web.render.AnnoErrorRenderEngine;
import net.hasor.test.web.render.SimpleRenderEngine;
import net.hasor.web.AbstractTest;
import net.hasor.web.render.Render;
import net.hasor.web.render.RenderEngine;
import net.hasor.web.render.RenderWebPlugin;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class RenderBinderTest extends AbstractTest {
    @Test
    public void renderTest_1() {
        String renderName = "name_name";
        BindInfo<? extends RenderEngine> bindInfo = bindInfo("bind_id", SimpleRenderEngine.class);
        RenderDef def = new RenderDef(renderName, bindInfo);
        //
        assert def.getID().equals("bind_id");
        assert def.getRenderName().equals(renderName);
        assert def.toString().startsWith("rendName=" + renderName);
    }

    @Test
    public void renderTest_2() throws Throwable {
        final SimpleRenderEngine testRenderEngine = new SimpleRenderEngine();
        final Supplier<SimpleRenderEngine> testRenderEngineProvider = () -> testRenderEngine;
        //
        AppContext appContext = buildWebAppContext(apiBinder -> {
            BindInfo<SimpleRenderEngine> engineBindInfo1 = apiBinder.bindType(SimpleRenderEngine.class).uniqueName().asEagerSingleton().toInfo();
            BindInfo<SimpleRenderEngine> engineBindInfo2 = apiBinder.bindType(SimpleRenderEngine.class).uniqueName().toInfo();
            //
            apiBinder.addRender("htm1").toInstance(testRenderEngine);           // 1
            apiBinder.addRender("htm2").toProvider(testRenderEngineProvider);   // 2
            apiBinder.addRender("htm3").to(SimpleRenderEngine.class);           // 3
            apiBinder.addRender("htm4").bindToInfo(engineBindInfo1);            // 4
            apiBinder.addRender("htm5").bindToInfo(engineBindInfo2);            // 5
            try {
                apiBinder.addRender("htm5").bindToInfo(engineBindInfo2);        // duplicate
                assert false;
            } catch (IllegalStateException e) {
                assert e.getMessage().startsWith("duplicate bind -> bindName 'htm5'");
            }
        }, servlet30("/"), LoadModule.Web);
        //
        List<RenderDef> definitions = appContext.findBindingBean(RenderDef.class);
        assert definitions.size() == 5;
        for (int i = 0; i < 5; i++) {
            assert definitions.get(i).getClass() == RenderDef.class;
        }
        //
        Object invoke1 = definitions.get(0).newEngine(appContext);     // 1
        Object invoke2 = definitions.get(1).newEngine(appContext);     // 2
        Object invoke3_1 = definitions.get(2).newEngine(appContext);   // 3
        Object invoke3_2 = definitions.get(2).newEngine(appContext);   // 3
        Object invoke4_1 = definitions.get(3).newEngine(appContext);   // 4
        Object invoke4_2 = definitions.get(3).newEngine(appContext);   // 4
        Object invoke5_1 = definitions.get(4).newEngine(appContext);   // 5
        Object invoke5_2 = definitions.get(4).newEngine(appContext);   // 5
        //
        assert invoke1 == invoke2;
        assert invoke3_1 != invoke3_2;
        assert invoke4_1 == invoke4_2;
        assert invoke5_1 != invoke5_2;
    }

    @Test
    public void renderTest_3() {
        AppContext appContext = buildWebAppContext(apiBinder -> {
            try {
                apiBinder.loadRender(BasicAction.class);
                assert false;
            } catch (Exception e) {
                assert e.getMessage().endsWith(" must be configure @Render");
            }
            try {
                apiBinder.loadRender(AppContext.class);
                assert false;
            } catch (Exception e) {
                assert e.getMessage().endsWith(" must be normal Bean");
            }
            //
            try {
                apiBinder.loadRender(AnnoErrorRenderEngine.class);
                assert false;
            } catch (Exception e) {
                assert e.getMessage().endsWith(" must be implements RenderEngine.");
            }
            //
            Set<Class<?>> classSet = apiBinder.findClass(Render.class, "net.hasor.test.web.render.*");
            assert classSet.size() == 2;
            classSet.remove(AnnoErrorRenderEngine.class); // remove Error
            apiBinder.loadRender(classSet);
        }, servlet30("/"), LoadModule.Web);
        //
        List<RenderDef> definitions = appContext.findBindingBean(RenderDef.class);
        assert definitions.size() == 1;
        //
        Set<String> suffixSet = new HashSet<>();
        suffixSet.add(definitions.get(0).getRenderName());
        //
        assert suffixSet.size() == 1;
        assert suffixSet.contains("jspx");
    }

    @Test
    public void renderTest_4() {
        AppContext appContext = buildWebAppContext(apiBinder -> {
            apiBinder.installModule(new RenderWebPlugin());
            apiBinder.addRender("htm").toInstance(PowerMockito.mock(RenderEngine.class));
        }, servlet30("/"), LoadModule.Web, LoadModule.Render);
        //
        List<FilterDef> defList = appContext.findBindingBean(FilterDef.class);
        assert defList.size() == 1;
        assert defList.get(0).getTargetType().getBindID().equals("net.hasor.web.render.RenderInvokerFilter");
        assert defList.get(0).getIndex() == Integer.MIN_VALUE;
    }
}
