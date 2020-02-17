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
package net.hasor.dataql.apibinder;
import net.hasor.core.Hasor;
import net.hasor.core.TypeSupplier;
import net.hasor.dataql.*;
import net.hasor.dataql.compiler.QueryModel;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.runtime.QueryHelper;
import net.hasor.test.dataql.udfs.AnnoDemoUdf;
import net.hasor.utils.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试用例
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
public class BinderTest extends AbstractTestResource {
    @Test
    public void typeSupplier_1_test() throws IOException {
        Map<Class<?>, Object> objectMap = new HashMap<>();
        TypeSupplier supplier = new TypeSupplier() {
            @Override
            public <T> T get(Class<? extends T> targetType) {
                if (targetType == AnnoDemoUdf.class) {
                    objectMap.put(targetType, new AnnoDemoUdf());
                    return (T) objectMap.get(targetType);
                }
                return null;
            }
        };
        //
        DataQL dataQL = Hasor.create().build((QueryModule) apiBinder -> {
            apiBinder.loadUdf(AnnoDemoUdf.class, supplier);
        }).getInstance(DataQL.class);
        //
        Object unwrap = dataQL.createQuery("return test();").execute().getData().unwrap();
        assert unwrap.equals("test");
        assert objectMap.size() == 1;
        assert objectMap.get(AnnoDemoUdf.class) != null;
    }
}