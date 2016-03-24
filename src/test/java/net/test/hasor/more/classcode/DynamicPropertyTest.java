/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.test.hasor.more.classcode;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.PropertyException;
import org.junit.Test;
import org.more.classcode.delegate.faces.MethodClassConfig;
import org.more.classcode.delegate.faces.MethodDelegate;
import org.more.classcode.delegate.property.PropertyClassConfig;
import org.more.classcode.delegate.property.PropertyDelegate;
import net.test.hasor.more.classcode.beans.TestBean2;
import net.test.hasor.more.classcode.beans.TestBean2_Face;
/**
 *
 * @version 2010-8-25
 * @author 赵永春 (zyc@hasor.net)
 */
public class DynamicPropertyTest {
    @Test
    public void testBaseTypePropxy() throws Exception {
        MethodClassConfig ce = new MethodClassConfig(TestBean2.class);
        ce.addDelegate(Remote.class, new Propxy_Propxy());
        Object obj = ce.toClass().newInstance();
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
        PropertyClassConfig ce = new PropertyClassConfig();
        ce.addProperty("name", String.class);
        Object obj = ce.toClass().newInstance();
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
        PropertyClassConfig ce = new PropertyClassConfig();
        ce.addProperty("name", new Propxy_NamePropertyDelegate());
        Object obj = ce.toClass().newInstance();
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
        MethodClassConfig ce = new MethodClassConfig();
        ce.addDelegate(Map.class, new Propxy_InterfaceDelegate());
        Map obj = (Map) ce.toClass().newInstance();
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
    public Object invoke(Method callMethod, Object target, Object[] params) throws Exception {
        Method m = target.getClass().getMethod(callMethod.getName(), callMethod.getParameterTypes());
        return m.invoke(target, params);
    }
}
class Propxy_InterfaceDelegate implements MethodDelegate {
    private HashMap<Object, Object> value = new HashMap<Object, Object>();
    public Object invoke(Method callMethod, Object target, Object[] params) {
        if (callMethod.getName().equals("get"))
            return this.value.get(params[0]);
        else if (callMethod.getName().equals("put"))
            this.value.put(params[0], params[1]);
        else
            System.out.println(callMethod);
        return null;
    }
}