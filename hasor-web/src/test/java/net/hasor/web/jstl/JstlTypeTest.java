package net.hasor.web.jstl;
import net.hasor.core.AppContext;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.hasor.web.invoker.AbstractWeb30BinderDataTest;
import net.hasor.web.invoker.params.QueryCallAction;
import net.hasor.web.jstl.taglib.DefineTypeTag;
import net.hasor.web.startup.RuntimeListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import static org.mockito.Matchers.anyObject;
//
@RunWith(PowerMockRunner.class)
@PrepareForTest({ RuntimeListener.class })
public class JstlTypeTest extends AbstractWeb30BinderDataTest {
    @Before
    public void beforeInit() throws Throwable {
        PowerMockito.mockStatic(RuntimeListener.class);
    }
    @Test
    public void chainTest1() throws Throwable {
        //
        AppContext appContext = hasor.build(new WebModule() {
            @Override
            public void loadModule(WebApiBinder apiBinder) throws Throwable {
                apiBinder.tryCast(WebApiBinder.class).loadMappingTo(QueryCallAction.class);
                apiBinder.bindType(String.class).toInstance("abcdefg");
            }
        });
        //
        PageContext pageContext = PowerMockito.mock(PageContext.class);
        PowerMockito.when(pageContext.getServletContext()).thenReturn(appContext.getInstance(ServletContext.class));
        PowerMockito.when(RuntimeListener.getAppContext((ServletContext) anyObject())).thenReturn(appContext);
        DefineTypeTag tag = new DefineTypeTag();
        tag.setPageContext(pageContext);
        //
        try {
            tag.doStartTag();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().contains("tag param var is null.");
        }
        //
        try {
            tag.setVar("abc");
            assert tag.getVar().equals("abc");
            tag.doStartTag();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().contains("tag param bindType is null.");
        }
        //
        try {
            tag.setBindType("java.lang.String");
            assert tag.getBindType().equals("java.lang.String");
            assert tag.doStartTag() == Tag.SKIP_BODY;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
        //
        tag.release();
        assert true;
    }
}