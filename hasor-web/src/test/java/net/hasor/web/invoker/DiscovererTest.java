package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.web.Mapping;
import net.hasor.web.MappingDiscoverer;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.hasor.web.invoker.params.QueryCallAction;
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
            apiBinder.tryCast(WebApiBinder.class).loadMappingTo(QueryCallAction.class);
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