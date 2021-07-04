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
package net.hasor.utils.supplier;
/**
 * 根据类型创建对象。
 * @version : 2020年2月27日
 * @author 赵永春 (zyc@byshell.org)
 */
@FunctionalInterface
public interface TypeSupplier {
    /** @return 获取对象。*/
    public <T> T get(Class<? extends T> targetType);

    /** 测试 TypeSupplier 是否支持这个类型，默认全部支持。 */
    public default <T> boolean test(Class<? extends T> targetType) {
        return true;
    }

    /** 将当前 TypeSupplier 串联到 other 的前面，如果 other 的 test 方法返回 false 就会执行当前这个。 */
    public default TypeSupplier beforeOther(TypeSupplier other) {
        return new TypeSupplier() {
            public <T> T get(Class<? extends T> targetType) {
                if (test(targetType)) {
                    return TypeSupplier.this.get(targetType);
                }
                return other.get(targetType);
            }

            public <T> boolean test(Class<? extends T> targetType) {
                return TypeSupplier.this.test(targetType);
            }
        };
    }

    /** 将当前 TypeSupplier 串联到 other 的后面，如果当前TypeSupplier的 test 方法返回 false 就会执行后面那个。 */
    public default TypeSupplier afterOther(TypeSupplier other) {
        return new TypeSupplier() {
            public <T> T get(Class<? extends T> targetType) {
                if (other.test(targetType)) {
                    return other.get(targetType);
                }
                return TypeSupplier.this.get(targetType);
            }

            public <T> boolean test(Class<? extends T> targetType) {
                return TypeSupplier.this.test(targetType);
            }
        };
    }
}
