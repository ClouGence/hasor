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
import net.hasor.core.Hasor;
import net.hasor.test.core.docs.CustomBean;
import net.hasor.test.core.docs.OrderManager;
import org.junit.Test;

public class DocsTest {
    @Test
    public void doc_1() {
        CustomBean customBean = Hasor.create().build().getInstance(CustomBean.class);
        assert customBean != null;
        assert customBean.callFoo() != null;
    }

    @Test
    public void doc_2() {
        OrderManager customBean = Hasor.create().build().getInstance(OrderManager.class);
        assert customBean.getStockBean() == null;
        assert customBean.getStockBeanTest() == null;
    }
}