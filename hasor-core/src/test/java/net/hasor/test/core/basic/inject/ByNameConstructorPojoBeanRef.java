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
package net.hasor.test.core.basic.inject;
import net.hasor.core.ConstructorBy;
import net.hasor.core.Inject;
import net.hasor.core.Type;
import net.hasor.test.core.basic.pojo.PojoBean;

//
public class ByNameConstructorPojoBeanRef {
    private PojoBean pojoBean;

    @ConstructorBy
    public ByNameConstructorPojoBeanRef(@Inject(value = "my_pojoBean", byType = Type.ByName) PojoBean pojoBean) {
        this.pojoBean = pojoBean;
    }

    public PojoBean getPojoBean() {
        return pojoBean;
    }

    public void setPojoBean(PojoBean pojoBean) {
        this.pojoBean = pojoBean;
    }
}
