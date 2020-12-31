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
package net.hasor.core.aop;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.hasor.core.Provider;
import net.hasor.utils.BeanUtils;
import org.junit.Test;

/**
 *
 * @version : 2020-09-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class PropertyTest {
    @Test
    public void propertyTest1() throws Exception {
        SimplePropertyDelegate delegate = new SimplePropertyDelegate();
        AopClassConfig classConfig = new AopClassConfig();
        classConfig.addProperty("name", String.class, Provider.of(delegate));
        //
        Class<?> buildClass = classConfig.buildClass();
        Object instance = buildClass.newInstance();
        //
        BeanUtils.writeProperty(instance, "name", "abc");
        assert JSON.toJSONString(instance, SerializerFeature.UseSingleQuotes).equals("{'name':'abc'}");
        BeanUtils.writeProperty(instance, "name", "def");
        assert JSON.toJSONString(instance, SerializerFeature.UseSingleQuotes).equals("{'name':'def'}");
    }

    @Test
    public void propertyTest2() throws Exception {
        AopClassConfig classConfig = new AopClassConfig();
        classConfig.addProperty("name", Boolean.TYPE);
        //
        Class<?> buildClass = classConfig.buildClass();
        Object instance = buildClass.newInstance();
        //
        BeanUtils.writeProperty(instance, "name", true);
        assert JSON.toJSONString(instance, SerializerFeature.UseSingleQuotes).equals("{'name':true}");
        BeanUtils.writeProperty(instance, "name", false);
        assert JSON.toJSONString(instance, SerializerFeature.UseSingleQuotes).equals("{'name':false}");
    }

    @Test
    public void propertyTest3() throws Exception {
        AopClassConfig classConfig = new AopClassConfig();
        classConfig.addProperty("name", Boolean.TYPE, ReadWriteType.ReadOnly);
        Class<?> buildClass = classConfig.buildClass();
        assert BeanUtils.canReadProperty("name", buildClass);
        assert !BeanUtils.canWriteProperty("name", buildClass);
    }

    @Test
    public void propertyTest4() throws Exception {
        AopClassConfig classConfig = new AopClassConfig();
        classConfig.addProperty("name", Boolean.TYPE, ReadWriteType.ReadWrite);
        Class<?> buildClass = classConfig.buildClass();
        assert BeanUtils.canReadProperty("name", buildClass);
        assert BeanUtils.canWriteProperty("name", buildClass);
    }
}