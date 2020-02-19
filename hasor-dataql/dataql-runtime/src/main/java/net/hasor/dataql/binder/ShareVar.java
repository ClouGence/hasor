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
package net.hasor.dataql.binder;
import java.util.function.Supplier;

/**
 * UDF 函数定义
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
class ShareVar implements Supplier {
    private String      varName;
    private Supplier<?> varSupplier;

    public ShareVar(String varName, Supplier<?> varSupplier) {
        this.varName = varName;
        this.varSupplier = varSupplier;
    }

    public String getName() {
        return this.varName;
    }

    @Override
    public Object get() {
        if (this.varSupplier != null) {
            return this.varSupplier.get();
        }
        return null;
    }
}