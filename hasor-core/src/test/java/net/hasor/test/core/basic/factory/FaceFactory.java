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
package net.hasor.test.core.basic.factory;
import net.hasor.utils.supplier.TypeSupplier;
import net.hasor.test.core.basic.pojo.SampleBean;
import net.hasor.test.core.basic.pojo.SampleFace;

/**
 * TypeSupplier
 * @version : 2020-09-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class FaceFactory implements TypeSupplier {
    private SampleBean target1 = new SampleBean();
    private SampleFace target2 = new SampleBean();

    public SampleBean getTarget1() {
        return target1;
    }

    public SampleFace getTarget2() {
        return target2;
    }

    public <T> T get(Class<? extends T> targetType) {
        if (targetType == SampleBean.class) {
            return (T) target1;
        }
        if (targetType == SampleFace.class) {
            return (T) target2;
        }
        return null;
    }
}
