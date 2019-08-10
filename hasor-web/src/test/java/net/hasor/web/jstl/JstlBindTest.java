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
package net.hasor.web.jstl;
import net.hasor.core.AppContext;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.hasor.web.invoker.AbstractWeb30BinderDataTest;
import net.hasor.test.actions.args.QueryArgsAction;
import net.hasor.web.jstl.taglib.DefineBindTag;
import net.hasor.web.startup.RuntimeListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import static org.mockito.Matchers.anyObject;
//
@RunWith(PowerMockRunner.class)
@PrepareForTest({ RuntimeListener.class })
public class JstlBindTest extends AbstractWeb30BinderDataTest {
    @Before
    public void beforeInit() throws Throwable {
        PowerMockito.mockStatic(RuntimeListener.class);
    }
    @Test
    public void chainTest1() throws Throwable {
        //
        AppContext appContext = hasor.build((WebModule) apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).loadMappingTo(QueryArgsAction.class);
            apiBinder.bindType(String.class).idWith("my_name_is").toInstance("abcdefg");
        });
        //
        PageContext pageContext = PowerMockito.mock(PageContext.class);
        PowerMockito.when(pageContext.getServletContext()).thenReturn(appContext.getInstance(ServletContext.class));
        PowerMockito.when(RuntimeListener.getAppContext(anyObject())).thenReturn(appContext);
        DefineBindTag tag = new DefineBindTag();
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
            tag.setName("my_name_is");
            assert tag.getName().equals("my_name_is");
            tag.doStartTag();
            assert false;
        } catch (Exception e) {
            assert e.getMessage().contains("tag param bindType is null.");
        }
        //
        try {
            tag.setBindType("abc.abc.abc.String");
            assert tag.doStartTag() == Tag.SKIP_BODY;
            assert false;
        } catch (JspException e) {
            assert e.getCause() instanceof ClassNotFoundException;
        }
        //
        try {
            tag.setBindType("java.lang.String");
            assert tag.getBindType().equals("java.lang.String");
        } catch (Exception e) {
            assert e.getMessage().contains("tag param name is null.");
        }
        //
        try {
            tag.setName("my_name_is");
            assert tag.getName().equals("my_name_is");
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