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
package net.hasor.core.bean;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.test.core.basic.factory.FaceFactory;
import net.hasor.test.core.basic.pojo.SampleBean;
import net.hasor.test.core.basic.pojo.SampleFace;
import org.junit.Test;

public class BeanTest {
    @Test
    public void beanProvider() {
        FaceFactory factory = new FaceFactory();
        //
        AppContext appContext = Hasor.create().build(apiBinder -> {
            apiBinder.bindType(SampleBean.class).toTypeSupplier(factory);
            apiBinder.bindType(SampleFace.class).toTypeSupplier(factory);
        });
        //
        SampleBean sampleBean1 = appContext.getInstance(SampleBean.class);
        SampleFace sampleBean2 = appContext.getInstance(SampleFace.class);
        //
        assert sampleBean1 != sampleBean2;
        assert factory.getTarget1() == sampleBean1;
        assert factory.getTarget2() == sampleBean2;
    }
}
