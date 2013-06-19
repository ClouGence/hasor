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
package org.test.el;
import java.util.Map;
import org.junit.Test;
import org.more.core.ognl.Node;
import org.more.core.ognl.Ognl;
import org.more.core.ognl.OgnlContext;
import org.more.core.ognl.OgnlException;
import org.more.util.attribute.AttBase;
/**
 * 
 * Date : 2011-4-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class TestEL {
    @Test
    public void test() throws OgnlException {
        Map<String, Object> m = new AttBase<Object>() {
            private static final long serialVersionUID = 5241838809363473602L;
            public Object get(Object key) {
                return super.get(key);
            }
            public Object put(String key, Object value) {
                return super.put(key, value);
            }
        };
        m.put("a", m);
        Node n = (Node) Ognl.parseExpression("a.$1");
        //        oc.setRoot(m);.
        //
        //        n.setValue(oc, m, 1);
        n.getValue(new OgnlContext(m), m);
        //        Ognl.getValue("$abc(1)", m);//#有特素意义
        //        Ognl.getValue("$abc(1)", m);//#有特素意义
    }
}