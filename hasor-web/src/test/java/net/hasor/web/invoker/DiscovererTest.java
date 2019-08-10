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
package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.hasor.test.actions.args.QueryArgsAction;
import net.hasor.web.wrap.DefaultServlet;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
//
public class DiscovererTest extends AbstractWeb30BinderDataTest {
    @Test
    public void sortTest3() throws Throwable {
        final ArrayList<String> discovererData = new ArrayList<>();
        //
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            apiBinder.addDiscoverer(mappingData -> discovererData.add(mappingData.getMappingTo()));
            //
            apiBinder.tryCast(WebApiBinder.class).jeeServlet("/*.do").with(1, new DefaultServlet());
            apiBinder.tryCast(WebApiBinder.class).jeeServlet("/*abc.do").with(0, new DefaultServlet());
            //
            apiBinder.tryCast(WebApiBinder.class).loadMappingTo(QueryArgsAction.class);
        });
        //
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new HashMap<String, String>() {{
            //
        }});
        //
        assert discovererData.size() == 3;
        assert discovererData.get(0).equals("/*abc.do");
        assert discovererData.get(1).equals("/query_param.do");
        assert discovererData.get(2).equals("/*.do");
    }
}