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
package org.test.bean;
import java.io.IOException;
import org.hasor.context.anno.context.AnnoAppContext;
public class CustomBeanTest {
    public static void main(String[] args) throws IOException {
        AnnoAppContext context = new AnnoAppContext();
        context.start();
        //
        CustomBean bean1 = context.getBean("myBean");
        AnnoCustomBean bean2 = context.getBean("myBeanAnno");
        //
        //CustomBean bean = context.getInstance(CustomBean.class);
        bean1.foo();
        bean2.foo();
    }
}