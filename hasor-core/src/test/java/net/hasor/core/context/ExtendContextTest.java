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
package net.hasor.core.context;
import net.hasor.core.*;
import net.hasor.test.core.basic.pojo.PojoBean;
import org.junit.Test;

public class ExtendContextTest {
    @Test
    public void test1() {
        PojoBean pojoBean = new PojoBean();
        TypeSupplier objectTypeSupplier = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(PojoBean.class).toInstance(pojoBean);
        }).wrapTypeSupplier();
        assert objectTypeSupplier.get(PojoBean.class) == pojoBean;
    }

    @Test
    public void test2() {
        AppContext appContext1 = Hasor.create().mainSettingWith("/net_hasor_core_context/startup1_exter.xml").build();
        assert appContext1.getInstance(String.class).equals("config");
        //
        AppContext appContext2 = Hasor.create().mainSettingWith("/net_hasor_core_context/startup2_exter.xml").build();
        assert appContext2.getInstance(String.class).equals("config");
        //
        AppContext appContext3 = Hasor.create().mainSettingWith("/net_hasor_core_context/startup3_exter.xml").build();
        assert appContext3.getInstance(String.class).equals("config");
        //
    }
}