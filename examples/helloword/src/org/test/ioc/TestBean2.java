/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.test.ioc;
import java.io.IOException;
import javax.inject.Inject;
import org.hasor.context.anno.context.AnnoAppContext;
import org.test.bean.CustomBean;
public class TestBean2 {
    private CustomBean customBean = null;
    @Inject
    public TestBean2(CustomBean customBean) {
        this.customBean = customBean;
    }
    public void callFoo() {
        this.customBean.foo();
    }
    public static void main(String[] args) throws IOException {
        AnnoAppContext context = new AnnoAppContext();
        context.start();
        TestBean2 bean = context.getInstance(TestBean2.class);
        bean.callFoo();
    }
}