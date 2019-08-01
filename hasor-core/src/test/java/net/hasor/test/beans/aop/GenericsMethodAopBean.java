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
package net.hasor.test.beans.aop;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @version : 2014-1-3
 * @author 赵永春 (zyc@hasor.net)
 */
public class GenericsMethodAopBean {
    public <T, V, Z> String fooCall1(T v1, V v2, Z v3) throws Exception {
        return "abc";
    }

    //
    public <T extends Date, V extends ArrayList> String fooCall2(T v1, V v2) throws Exception {
        return "abc";
    }

    //
    public String fooCall3(Class<? extends Date> v1, List<? extends Map<String, Date>> v2) throws Exception {
        return "abc";
    }
}