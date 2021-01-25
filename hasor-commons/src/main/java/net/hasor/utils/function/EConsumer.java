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
package net.hasor.utils.function;
import net.hasor.utils.ExceptionUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * Consumer 允许异常抛出。
 * @version 2021-01-23
 * @author 赵永春 (zyc@hasor.net)
 */
@FunctionalInterface
public interface EConsumer<T> extends Consumer<T> {
    @Override
    default void accept(T t) {
        try {
            this.eAccept(t);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    void eAccept(T t) throws IOException, SQLException;
}
