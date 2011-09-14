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
package org.test.more.core.copy;
import org.more.core.copybean.CopyBeanUtil;
public class Main {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Bean1 b1 = new Bean1();
        b1.setName("aaaa");
        System.out.println(b1.getName());
        //
        CopyBeanUtil copy = CopyBeanUtil.newInstance();
        //
        Bean2 b2 = new Bean2();
        copy.copy(b1, b2);
        System.out.println(b2.name);
        //
        Bean3 b3 = new Bean3();
        copy.copy(b1, b3);
        System.out.println(b3.getName());
        //
        Bean4 b4 = new Bean4();
        copy.copy(b1, b4);
        System.out.println(b4.name);
    }
}
