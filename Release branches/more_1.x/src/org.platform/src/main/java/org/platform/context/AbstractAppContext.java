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
package org.platform.context;
import org.platform.Assert;
import com.google.inject.Injector;
/**
 * {@link AppContext}接口的实现类。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public class AbstractAppContext extends AppContext {
    private Injector guice = null;
    protected AbstractAppContext(Injector guice) {
        this.guice = guice;
        Assert.isNotNull(guice);
    }
    @Override
    public Injector getGuice() {
        return this.guice;
    }
    //    @Override
    //    public <T> Class<T> getBeanType(String name) {
    //        // TODO Auto-generated method stub
    //        return null;
    //    }
    //    @Override
    //    public List<String> getBeanNames() {
    //        // TODO Auto-generated method stub
    //        return null;
    //    }
}