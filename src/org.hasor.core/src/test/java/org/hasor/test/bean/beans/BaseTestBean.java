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
package org.hasor.test.bean.beans;
import org.hasor.context.anno.Bean;
/**
 * AnnoServiceA 具有两个名称 AnnoA 和 BeanA
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
@Bean(value = { "AnnoA", "BeanA" })
public class BaseTestBean {
    public void foo() {
        System.out.println("this bean is AnnoA or BeanA type:" + this);
    }
}