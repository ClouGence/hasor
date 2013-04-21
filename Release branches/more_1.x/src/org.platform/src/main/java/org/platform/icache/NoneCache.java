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
package org.platform.icache;
import org.platform.context.AppContext;
import org.platform.context.setting.Config;
/**
 * √ª”–ª∫¥Ê°£
 * @version : 2013-4-20
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@DefaultCache()
@Cache({ "default" })
public class NoneCache implements ICache {
    @Override
    public void toCache(String key, Object value) {}
    @Override
    public void toCache(String key, Object value, long timeout) {}
    @Override
    public Object fromCache(String key) {
        return null;
    }
    @Override
    public boolean hasCache(String key) {
        return false;
    }
    @Override
    public void remove(String key) {}
    @Override
    public void clear() {}
    @Override
    public void initCache(AppContext appContext, Config config) {}
    @Override
    public void destroy() {}
}