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
package net.hasor.db.jdbc.paramer;
import net.hasor.db.jdbc.core.ParameterDisposer;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dto.TB_User;
import net.hasor.utils.BeanUtils;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static net.hasor.test.db.utils.TestUtils.beanForData1;

/***
 *
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class ParamsTest extends AbstractDbTest {
    @Test
    public void testParams_1() {
        TB_User tb_user = beanForData1();
        BeanSqlParameterSource beanParams = new BeanSqlParameterSource(tb_user);
        //
        String[] parameterNames = beanParams.getParameterNames();
        Set<String> params = new HashSet<>(Arrays.asList(parameterNames));
        assert params.contains("userUUID");
        assert params.contains("name");
        assert params.contains("loginName");
        assert params.contains("loginPassword");
        assert params.contains("email");
        assert params.contains("index");
        assert params.contains("registerTime");
        assert !params.contains("hashCode");
        //
        assert beanParams.hasValue("userUUID");
        assert beanParams.hasValue("name");
        assert beanParams.hasValue("loginName");
        assert beanParams.hasValue("loginPassword");
        assert beanParams.hasValue("email");
        assert beanParams.hasValue("index");
        assert beanParams.hasValue("registerTime");
        assert !beanParams.hasValue("hashCode");
        //
        assert tb_user.getUserUUID().equals(beanParams.getValue("userUUID"));
        //
        beanParams.cleanupParameters();
    }

    @Test
    public void testParams_2() {
        TB_User tb_user = beanForData1();
        Map<String, Object> dataMap = new HashMap<>();
        BeanUtils.copyProperties(dataMap, tb_user);
        //
        MapSqlParameterSource beanParams = new MapSqlParameterSource(dataMap);
        //
        String[] parameterNames = beanParams.getParameterNames();
        Set<String> params = new HashSet<>(Arrays.asList(parameterNames));
        assert params.contains("userUUID");
        assert params.contains("name");
        assert params.contains("loginName");
        assert params.contains("loginPassword");
        assert params.contains("email");
        assert params.contains("index");
        assert params.contains("registerTime");
        assert !params.contains("hashCode");
        //
        assert beanParams.hasValue("userUUID");
        assert beanParams.hasValue("name");
        assert beanParams.hasValue("loginName");
        assert beanParams.hasValue("loginPassword");
        assert beanParams.hasValue("email");
        assert beanParams.hasValue("index");
        assert beanParams.hasValue("registerTime");
        assert !beanParams.hasValue("hashCode");
        //
        assert tb_user.getUserUUID().equals(beanParams.getValue("userUUID"));
        //
        beanParams.cleanupParameters();
    }

    @Test
    public void testParams_3() {
        AtomicBoolean supplierValue = new AtomicBoolean();
        AtomicBoolean clearValue = new AtomicBoolean();
        Map<String, Object> map = new HashMap<>();
        map.put("supplier", (Supplier<String>) () -> {
            supplierValue.set(true);
            return null;
        });
        map.put("clear", (ParameterDisposer) () -> clearValue.set(true));
        //
        MapSqlParameterSource parameter = new MapSqlParameterSource(map);
        //
        assert !supplierValue.get();
        assert !clearValue.get();
        //
        parameter.getValue("supplier");
        parameter.getValue("clear");
        assert supplierValue.get();
        assert !clearValue.get();
        //
        parameter.cleanupParameters();
        assert supplierValue.get();
        assert clearValue.get();
    }

    @Test
    public void testParams_4() {
        AtomicBoolean clearValue = new AtomicBoolean();
        Object objects = (ParameterDisposer) () -> clearValue.set(true);
        BeanSqlParameterSource parameter = new BeanSqlParameterSource(objects);
        //
        parameter.cleanupParameters();
        //
        assert clearValue.get();
    }
}
