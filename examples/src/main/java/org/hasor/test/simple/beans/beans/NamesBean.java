/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.hasor.test.simple.beans.beans;
import net.hasor.plugins.bean.Bean;
/**
 * AnnoServiceA 具有两个名称 name1 和 name2
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
@Bean(value = { "name1", "name2" })
public class NamesBean {
    public void foo() {
        System.out.println("this bean is name1 or name2 type:" + this);
    }
}