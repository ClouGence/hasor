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
package net.hasor.test.core.docs;
import net.hasor.core.AppContext;
import net.hasor.core.Inject;
import net.hasor.core.spi.InjectMembers;

public class OrderManager implements InjectMembers {
    @Inject  // <-因为实现了InjectMembers接口，因此@Inject注解将会失效。
    public CustomBean stockBeanTest;
    public CustomBean stockBean;

    public void doInject(AppContext appContext) throws Throwable {
        //
    }

    public CustomBean getStockBeanTest() {
        return stockBeanTest;
    }

    public CustomBean getStockBean() {
        return stockBean;
    }
}
