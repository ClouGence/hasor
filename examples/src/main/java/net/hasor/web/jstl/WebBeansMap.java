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
package net.hasor.web.jstl;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;
import net.hasor.core.AppContext;
/**
 * 
 * @version : 2013-12-23
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class WebBeansMap extends AbstractMap<String, Object> {
    private HashSet<Entry<String, Object>> entrySet = new HashSet<Entry<String, Object>>();
    //
    public WebBeansMap(AppContext appContext) {
        String[] names = appContext.getBeanNames();
        for (String name : names) {
            entrySet.add(new BeanEntry(name, appContext));
        }
    }
    //
    public Set<Entry<String, Object>> entrySet() {
        return this.entrySet;
    }
    private static class BeanEntry implements Entry<String, Object> {
        private String     name       = null;
        private AppContext appContext = null;
        public BeanEntry(String name, AppContext appContext) {
            this.name = name;
            this.appContext = appContext;
        }
        public String getKey() {
            return this.name;
        }
        public Object getValue() {
            return this.appContext.getBean(this.name);
        }
        public Object setValue(Object value) {
            throw new UnsupportedOperationException();
        }
    }
}