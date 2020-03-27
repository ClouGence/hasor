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
package net.hasor.core.context;
import net.hasor.core.*;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.test.core.basic.inject.PropertyPojoBeanRef;
import net.hasor.test.core.basic.inject.constructor.SingleConstructorPojoBeanRef;
import net.hasor.test.core.basic.pojo.PojoBean;
import org.junit.Test;

import java.lang.reflect.Constructor;

public class InjectContextTest {
    @Test
    public void builderTest1() throws Throwable {
        AppContext appContext1 = new AppContextWarp(Hasor.create().asTiny().build());
        //
        String[] bindIDs = appContext1.getBindIDs();
        assert bindIDs.length == 5;
        assert bindIDs[0].equals("net.hasor.core.Environment");
        assert bindIDs[1].equals("net.hasor.core.AppContext");
        assert bindIDs[2].equals("net.hasor.core.EventContext");
        assert bindIDs[3].equals("net.hasor.core.Settings");
        assert bindIDs[4].equals("net.hasor.core.spi.SpiTrigger");
        assert appContext1.getProvider(PojoBean.class).get() instanceof PojoBean;
        //
        Environment env = new StandardEnvironment();
        AppContext appContext2 = new AppContextWarp(new StatusAppContext(env));
        PojoBean mockBean = new PojoBean();
        appContext2.start(apiBinder -> {
            apiBinder.bindType(PojoBean.class).idWith("pojoBean").toInstance(mockBean);
            apiBinder.bindType(SingleConstructorPojoBeanRef.class)//
                    .toConstructor(SingleConstructorPojoBeanRef.class.getConstructor(PojoBean.class))//
                    .injectValue(0, new PojoBean());
        });
        //
        assert appContext2.getBindInfo("pojoBean") != null;
        assert appContext2.getBindInfo("pojoBean").getBindType().equals(PojoBean.class);
        assert appContext2.getBeanType("pojoBean") == PojoBean.class;
        assert appContext2.getBeanType("abc") == null;
        //
        assert appContext2.containsBindID("pojoBean");
        assert !appContext2.containsBindID("abc");
        //
        assert appContext2.getProvider("pojoBean").get() instanceof PojoBean;
        assert appContext2.getProvider("abc") == null;
        //
        BindInfo<Object> bindInfo = appContext2.getBindInfo("pojoBean");
        assert appContext2.getProvider(bindInfo).get() == mockBean;
        assert appContext2.getProvider((BindInfo) null) == null;
        assert appContext2.getProvider(PojoBean.class).get() == mockBean;
        //
        //
        Constructor<SingleConstructorPojoBeanRef> constructor = SingleConstructorPojoBeanRef.class.getConstructor(PojoBean.class);
        assert appContext1.getProvider(constructor).get() instanceof SingleConstructorPojoBeanRef;
        assert appContext2.getProvider(constructor).get() instanceof SingleConstructorPojoBeanRef;
        //
        //
        assert appContext1.justInject(null) == null;
        assert appContext1.justInject(null, PropertyPojoBeanRef.class) == null;
        PropertyPojoBeanRef ref = new PropertyPojoBeanRef();
        assert ref.getPojoBean() == null;
        assert appContext1.justInject(ref) == ref;
        assert ref.getPojoBean() != null && ref.getPojoBean() != mockBean;
        //
        ref.setPojoBean(null);
        assert appContext2.justInject(ref) == ref;
        assert ref.getPojoBean() == mockBean;
        //
        //
        PojoBean mockBean2 = new PojoBean();
        AppContext appContext3 = new AppContextWarp(new StatusAppContext(env));
        appContext3.start(apiBinder -> {
            apiBinder.bindType(PojoBean.class).toInstance(mockBean);
            apiBinder.bindType(PropertyPojoBeanRef.class).overwriteAnnotation()//
                    .injectValue("pojoBean", mockBean2);
        });
        ref.setPojoBean(null);
        assert appContext3.justInject(ref) == ref;
        assert ref.getPojoBean() == mockBean2;
        //
        BindInfo<PropertyPojoBeanRef> info = appContext3.getBindInfo(PropertyPojoBeanRef.class);
        ref.setPojoBean(null);
        assert appContext3.justInject(ref, info) == ref;
        assert ref.getPojoBean() == mockBean2;
        //
        ref.setPojoBean(null);
        assert appContext3.justInject(ref, (BindInfo<?>) null) == ref;
        assert ref.getPojoBean() == null;
    }
}