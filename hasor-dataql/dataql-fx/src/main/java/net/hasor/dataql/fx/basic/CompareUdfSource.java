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
package net.hasor.dataql.fx.basic;
import net.hasor.dataql.UdfSourceAssembly;

import javax.inject.Singleton;

/**
 * 比较函数。函数库引入 <code>import 'net.hasor.dataql.fx.basic.CompareUdfSource' as compare;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-31
 */
@Singleton
public class CompareUdfSource implements UdfSourceAssembly {
    /** 集合 或 Map 是否为空 */
    public static int compareString(String str1, String str2) {
        str1 = str1 == null ? "" : str1;
        str2 = str2 == null ? "" : str2;
        return str1.compareTo(str2);
    }

    /** 集合 或 Map 是否为空 */
    public static int compareStringIgnoreCase(String str1, String str2) {
        str1 = str1 == null ? "" : str1;
        str2 = str2 == null ? "" : str2;
        return str1.compareToIgnoreCase(str2);
    }
}