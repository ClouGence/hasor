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
import net.hasor.core.Singleton;
import net.hasor.dataql.UdfSourceAssembly;


/**
 * 数学函数。函数库引入 <code>import 'net.hasor.dataql.fx.basic.NumberUdfSource' as number;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
@Singleton
public class NumberUdfSource implements UdfSourceAssembly {
    public static int inRange(int value, int min, int max) {
        if (Math.min(value, min) == value) {
            return min;
        } else if (Math.max(value, max) == value) {
            return max;
        } else {
            return value;
        }
    }
}
