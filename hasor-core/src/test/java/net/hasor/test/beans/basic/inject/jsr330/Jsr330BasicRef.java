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
package net.hasor.test.beans.basic.inject.jsr330;
import net.hasor.test.beans.basic.pojo.PojoBean;

import javax.inject.Inject;

public class Jsr330BasicRef {
    @ByOrder
    private PojoBean pojoBean1;
    //
    @Inject
    private PojoBean pojoBean2;
    //
    @Inject
    @ByOrder
    private PojoBean pojoBean3;
    //
    @ByOrder
    @Inject
    private PojoBean pojoBean4;
    //
    //
    //
    @ByOrder
    @Noise
    @Inject
    private PojoBean pojoBean5;
    @ByOrder
    @Inject
    @Noise
    private PojoBean pojoBean6;
}