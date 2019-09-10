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
package net.hasor.web.valid;
import net.hasor.core.AppContext;
import net.hasor.test.actions.valid.ValidRequestFieldAction;
import net.hasor.web.AbstractTest;
import net.hasor.web.WebApiBinder;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class UnexpectedValidTest extends AbstractTest {
    @Test
    public void unexpected_1() throws Throwable {
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/valid_param.do").with(ValidRequestFieldAction.class);
        }, servlet25("/"), LoadModule.Web, LoadModule.Valid);
        //
        HttpServletRequest request = mockRequest("put", new URL("http://www.hasor.net/valid_param.do?byteParam=123&intParam=321&strParam=5678"));
        Object o = callInvoker(appContext, request);
        assert o instanceof Map;
        assert (Byte) ((Map) o).get("byteParam") == (byte) 123;
        assert ((Integer) ((Map) o).get("intParam")) == 321;
        assert ((List) ((Map) o).get("validErrorsOfString")).size() == 4;
        assert !((Boolean) ((Map) o).get("doValid"));
        //
        List validErrorsOfString = (List) ((Map) o).get("validErrorsOfString");
        assert validErrorsOfString.get(0).equals("max out of %s");
        assert validErrorsOfString.get(1).equals("max out of %d");
        //
        assert ((Map) o).get("err3_1") != null;
        assert ((Map) o).get("err3_1").equals("message 1");
        //
        assert ((List) ((Map) o).get("err3_2")).size() == 2;
        assert ((List) ((Map) o).get("err3_2")).get(0).equals("message 1");
        assert ((List) ((Map) o).get("err3_2")).get(1).equals("message 2");
    }

    @Test
    public void unexpected_2() throws Throwable {
        AppContext appContext = buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/valid_param.do").with(ValidRequestFieldAction.class);
        }, servlet25("/"), LoadModule.Web, LoadModule.Valid);
        //
        {
            HttpServletRequest request = mockRequest("head", new URL("http://www.hasor.net/valid_param.do?byteParam=123&intParam=321&strParam=5678"));
            Object o = callInvoker(appContext, request);
            assert o instanceof Map;
            assert !((Boolean) ((Map) o).get("err3_res_before"));
            assert (Boolean) ((Map) o).get("err3_res_after");
        }
    }
}