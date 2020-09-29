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
package net.hasor.core.delegate;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.aop.SimplePropertyDelegate;
import net.hasor.test.core.basic.pojo.PojoBean;
import net.hasor.test.core.basic.pojo.SampleBean;
import net.hasor.utils.BeanUtils;
import org.junit.Test;

/**
 *
 * @version : 2020-09-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class PropertyDelegateTest {
    @Test
    public void propertyTest1() {
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(PojoBean.class).dynamicProperty("dynamicName", String.class);
        });
        PojoBean pojoBean = appContext.getInstance(PojoBean.class);
        SampleBean sampleBean = appContext.getInstance(SampleBean.class);
        //
        assert !BeanUtils.hasPropertyOrField("dynamicName", SampleBean.class);
        assert !BeanUtils.hasPropertyOrField("dynamicName", sampleBean.getClass());
        assert !BeanUtils.hasPropertyOrField("dynamicName", PojoBean.class);
        assert BeanUtils.hasPropertyOrField("dynamicName", pojoBean.getClass());
        //
        BeanUtils.writeProperty(pojoBean, "dynamicName", "abc");
        assert JSON.toJSONString(pojoBean, SerializerFeature.UseSingleQuotes).equals("{'dynamicName':'abc'}");
        BeanUtils.writeProperty(pojoBean, "dynamicName", "def");
        assert JSON.toJSONString(pojoBean, SerializerFeature.UseSingleQuotes).equals("{'dynamicName':'def'}");
    }

    @Test
    public void propertyTest2() {
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(PojoBean.class).dynamicProperty("dynamicName", Boolean.TYPE);
        });
        PojoBean pojoBean = appContext.getInstance(PojoBean.class);
        //
        BeanUtils.writeProperty(pojoBean, "dynamicName", true);
        assert JSON.toJSONString(pojoBean, SerializerFeature.UseSingleQuotes).equals("{'dynamicName':true}");
        BeanUtils.writeProperty(pojoBean, "dynamicName", false);
        assert JSON.toJSONString(pojoBean, SerializerFeature.UseSingleQuotes).equals("{'dynamicName':false}");
    }

    @Test
    public void propertyTest3() throws Exception {
        SimplePropertyDelegate delegate = new SimplePropertyDelegate(123.123d);
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(PojoBean.class).dynamicReadOnlyProperty("dynamicName", Double.TYPE, delegate);
        });
        PojoBean pojoBean = appContext.getInstance(PojoBean.class);
        //
        assert BeanUtils.canReadPropertyOrField("dynamicName", pojoBean.getClass());
        assert !BeanUtils.canWritePropertyOrField("dynamicName", pojoBean.getClass());
        //
        // 没有写属性
        BeanUtils.writeProperty(pojoBean, "dynamicName", 12);
        assert JSON.toJSONString(pojoBean, SerializerFeature.UseSingleQuotes).equals("{'dynamicName':123.123}");
        // 更改 delegate 值被修改
        delegate.setValue(321.321);
        assert JSON.toJSONString(pojoBean, SerializerFeature.UseSingleQuotes).equals("{'dynamicName':321.321}");
    }
}
