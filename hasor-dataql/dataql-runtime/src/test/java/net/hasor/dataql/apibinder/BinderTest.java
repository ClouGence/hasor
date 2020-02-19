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
import net.hasor.core.exts.aop.Matchers;
import net.hasor.dataql.*;
import net.hasor.test.dataql.udfs.AnnoDemoUdf;
import net.hasor.test.dataql.udfs.TimeUdfSource;
import net.hasor.utils.ExceptionUtils;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试用例
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-19
 */
public class BinderTest extends AbstractTestResource {
    private Map<Class<?>, Object> objectMap = new HashMap<>();
    private TypeSupplier          supplier  = new TypeSupplier() {
        @Override
        public <T> T get(Class<? extends T> targetType) {
            try {
                objectMap.put(targetType, targetType.newInstance());
                return (T) objectMap.get(targetType);
            } catch (Exception e) {
                throw ExceptionUtils.toRuntimeException(e);
            }
        }
    };

    @Test
    public void typeSupplier_1_test() throws IOException {
        objectMap.clear();
        DataQL dataQL = Hasor.create().build((QueryModule) apiBinder -> {
            apiBinder.loadUdf(apiBinder.findClass(DimUdf.class), Matchers.anyClass(), supplier);
        }).getInstance(DataQL.class);
        //
        Object unwrap = dataQL.createQuery("return test();").execute().getData().unwrap();
        assert unwrap.equals("test");
        assert objectMap.size() == 1;
        assert objectMap.get(AnnoDemoUdf.class) != null;
    }

    @Test
    public void typeSupplier_2_test() throws IOException {
        objectMap.clear();
        DataQL dataQL = Hasor.create().build((QueryModule) apiBinder -> {
            apiBinder.loadUdfSource(apiBinder.findClass(DimUdfSource.class), Matchers.anyClass(), supplier);
        }).getInstance(DataQL.class);
        //
        long t = System.currentTimeMillis();
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date(t));
        //
        Object unwrap = dataQL.createQuery("return time.ymd(" + t + ");").execute().getData().unwrap();
        assert unwrap.equals(format);
        assert objectMap.size() == 1;
        assert objectMap.get(TimeUdfSource.class) != null;
    }
}