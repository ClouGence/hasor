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
import net.hasor.test.beans.aop.ignore.types.GrandFatherBean;
import net.hasor.test.beans.aop.ignore.types.JamesBean;
import net.hasor.test.beans.aop.ignore.types.WilliamSonBean;
import net.hasor.test.beans.basic.inject.constructor.NativeConstructorPojoBeanRef;
import net.hasor.test.beans.basic.inject.constructor.SingleConstructorPojoBeanRef;
import net.hasor.test.beans.basic.pojo.PojoBean;
import org.junit.Test;

import java.lang.reflect.Constructor;

/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class ContextHasorApiTest {
    @Test
    public void contextTest1() {
        AppContext appContext = new AppContextWarp(Hasor.create().build((Module) apiBinder -> {
            apiBinder.bindType(PojoBean.class).bothWith("pojo");
            apiBinder.bindType(GrandFatherBean.class).nameWith("james").to(JamesBean.class);
            apiBinder.bindType(GrandFatherBean.class).nameWith("william").to(WilliamSonBean.class);
        }));
        //
        String[] names = appContext.getNames(GrandFatherBean.class);
        assert names.length == 2;
        assert names[0].equals("james");
        assert names[1].equals("william");
        //
        assert appContext.findBindingBean(PojoBean.class).size() == 1;
        assert appContext.findBindingBean(GrandFatherBean.class).size() == 2;
        //
        assert appContext.findBindingProvider(PojoBean.class).size() == 1;
        assert appContext.findBindingProvider(GrandFatherBean.class).size() == 2;
        //
        assert appContext.findBindingBean("pojo", PojoBean.class) != null;
        assert appContext.findBindingBean("james", GrandFatherBean.class) instanceof JamesBean;
        assert appContext.findBindingBean("", GrandFatherBean.class) == null;
        //
        assert appContext.findBindingProvider("pojo", PojoBean.class).get() != null;
        assert appContext.findBindingProvider("james", GrandFatherBean.class).get() instanceof JamesBean;
        assert appContext.findBindingProvider("", GrandFatherBean.class) == null;
    }

    @Test
    public void contextTest2() {
        AppContext appContext = new AppContextWarp(Hasor.create().build((Module) apiBinder -> {
            apiBinder.bindType(PojoBean.class).bothWith("pojo").asEagerSingleton();
        }));
        //
        PojoBean pojoBean1 = appContext.getInstance("pojo");
        assert pojoBean1 instanceof PojoBean;
        //
        BindInfo<PojoBean> register = appContext.findBindingRegister("pojo", PojoBean.class);
        PojoBean pojoBean2 = appContext.getInstance(register);
        assert pojoBean2 instanceof PojoBean;
        assert pojoBean1 == pojoBean2;
        //
        assert appContext.getInstance("abc") == null;
    }

    @Test
    public void contextTest3() {
        AppContext appContext = new AppContextWarp(() -> Hasor.create().build());
        //
        PojoBean pojoBean = new PojoBean();
        SingleConstructorPojoBeanRef refBean = appContext.getInstance(SingleConstructorPojoBeanRef.class, pojoBean);
        assert refBean instanceof SingleConstructorPojoBeanRef;
        assert refBean.getPojoBean() == pojoBean;
    }

    @Test
    public void contextTest4() throws NoSuchMethodException {
        AppContext appContext = new AppContextWarp(() -> Hasor.create().build());
        //
        PojoBean pojoBean = new PojoBean();
        Constructor<SingleConstructorPojoBeanRef> refConstructor = SingleConstructorPojoBeanRef.class.getConstructor(PojoBean.class);
        SingleConstructorPojoBeanRef refBean = appContext.getInstance(refConstructor, pojoBean);
        assert refBean instanceof SingleConstructorPojoBeanRef;
        assert refBean.getPojoBean() == pojoBean;
    }

    @Test
    public void contextTest5() throws NoSuchMethodException {
        AppContext appContext = new AppContextWarp(() -> Hasor.create().build());
        //
        PojoBean pojoBean = new PojoBean();
        Constructor<NativeConstructorPojoBeanRef> refConstructor = NativeConstructorPojoBeanRef.class.getConstructor(PojoBean.class);
        NativeConstructorPojoBeanRef refBean = appContext.getInstance(refConstructor, pojoBean);
        assert refBean instanceof NativeConstructorPojoBeanRef;
        assert refBean.getPojoBean() == pojoBean;
    }

    @Test
    public void contextTest6() {
        AppContext context = Hasor.create().build();
        AppContextWarp appContext = new AppContextWarp(() -> context);
        //
        assert appContext.getAppContext() == context;
        assert appContext.getEnvironment() == context.getEnvironment();
        //
        appContext.setMetaData("abc", "abc");
        assert "abc".equals(appContext.getMetaData("abc"));
        appContext.removeMetaData("abc");
        assert appContext.getMetaData("abc") == null;
    }
}
