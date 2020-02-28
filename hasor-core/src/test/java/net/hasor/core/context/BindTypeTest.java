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
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import org.junit.Test;

public class BindTypeTest {
    @Test
    public void test1() {
        AppContext appContext = Hasor.create().asCore().build(apiBinder -> {
            apiBinder.bindType(BindTypeTest.class).uniqueName().toInstance(new BindTypeTest());
            apiBinder.bindType(BindTypeTest.class).uniqueName().toInstance(new BindTypeTest());
        });
        assert appContext.findBindingBean(BindTypeTest.class).size() == 2;
    }
}
