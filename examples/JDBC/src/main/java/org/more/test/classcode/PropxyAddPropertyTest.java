/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.more.test.classcode;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.more.classcode.BuilderMode;
import org.more.classcode.ClassEngine;
import org.more.classcode.InvokeException;
import org.more.classcode.MethodDelegate;
import org.more.classcode.PropertyDelegate;
import org.more.classcode.PropertyException;
/**
 *
 * @version 2010-8-25
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
public class PropxyAddPropertyTest {
    @Test
    public void testBaseTypePropxy() throws Exception {
        ClassEngine ce = new ClassEngine();
        ce.setSuperClass(TestBean2.class);
        ce.setBuilderMode(BuilderMode.Propxy);
        ce.addDelegate(Remote.class, new Propxy_Propxy());
        Object obj = ce.newInstance(new TestBean2());
        obj.getClass().getInterfaces();
        //
        TestBean2_Face face = (TestBean2_Face) obj;
        //
        face.setP_double(12);
        face.getP_double();
        face.setP_float(12.56f);
        face.getP_float();
        face.setP_long(13l);
        face.getP_long();
    }
    @Test
    public void addPropertyBase() throws Exception {
        ClassEngine ce = new ClassEngine();
        ce.setBuilderMode(BuilderMode.Propxy);
        ce.addProperty("name", String.class);
        Object obj = ce.newInstance(new Object());
        //
        Method wm = obj.getClass().getMethod("setName", new Class[] { String.class });
        Method rm = obj.getClass().getMethod("getName");
        //
        System.out.println(rm.invoke(obj));
        wm.invoke(obj, new Object[] { "affa" });
        System.out.println(rm.invoke(obj));
    };
    @Test
    public void addPropertyDelegate() throws Exception {
        ClassEngine ce = new ClassEngine();
        ce.setBuilderMode(BuilderMode.Propxy);
        ce.addProperty("name", new Propxy_NamePropertyDelegate());
        Object obj = ce.newInstance(new Object());
        //
        Method wm = obj.getClass().getMethod("setName", new Class[] { String.class });
        Method rm = obj.getClass().getMethod("getName");
        //
        System.out.println(rm.invoke(obj));
        wm.invoke(obj, new Object[] { "affa" });
        System.out.println(rm.invoke(obj));
    };
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInterface() throws Exception {
        ClassEngine ce = new ClassEngine();
        ce.setBuilderMode(BuilderMode.Propxy);
        ce.addDelegate(Map.class, new Propxy_InterfaceDelegate());
        Map obj = (Map) ce.newInstance(new Object());
        System.out.println(obj.get("name"));
        obj.put("name", "fuck");
        System.out.println(obj.get("name"));
    }
}
class Propxy_NamePropertyDelegate implements PropertyDelegate<String> {
    private String value = null;
    public Class<? extends String> getType() {
        return String.class;
    }
    public String get(Object target) throws PropertyException {
        return this.value;
    }
    public void set(Object target, String newValue) throws PropertyException {
        this.value = newValue;
    }
}
class Propxy_Propxy implements MethodDelegate {
    public Object invoke(Method callMethod, Object target, Object[] params) throws InvokeException {
        try {
            Method m = target.getClass().getMethod(callMethod.getName(), callMethod.getParameterTypes());
            return m.invoke(target, params);
        } catch (Exception e) {
            throw new InvokeException(e);
        }
    }
}
class Propxy_InterfaceDelegate implements MethodDelegate {
    private HashMap<Object, Object> value = new HashMap<Object, Object>();
    public Object invoke(Method callMethod, Object target, Object[] params) throws InvokeException {
        if (callMethod.getName().equals("get"))
            return this.value.get(params[0]);
        else if (callMethod.getName().equals("put"))
            this.value.put(params[0], params[1]);
        else
            System.out.println(callMethod);
        return null;
    }
}