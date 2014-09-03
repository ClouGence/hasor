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
package net.hasor.core.factorys.guice;
import net.hasor.core.Provider;
import net.hasor.core.Scope;
import com.google.inject.Key;
/**
 * 负责net.hasor.core.Scope与com.google.inject.Scope的对接转换
 * @version : 2014年9月3日
 * @author 赵永春(zyc@hasor.net)
 */
class GuiceScopeAdapter implements com.google.inject.Scope {
    private Scope scope = null;
    public GuiceScopeAdapter(final Scope scope) {
        this.scope = scope;
    }
    public String toString() {
        return this.scope.toString();
    };
    public <T> com.google.inject.Provider<T> scope(final Key<T> key, final com.google.inject.Provider<T> unscoped) {
        Provider<T> returnData = this.scope.scope(key, new ToHasorProviderAdapter<T>(unscoped));
        if (returnData instanceof com.google.inject.Provider) {
            return (com.google.inject.Provider<T>) returnData;
        } else if (returnData instanceof ToHasorProviderAdapter) {
            return ((ToHasorProviderAdapter) returnData).getProvider();
        } else {
            return new ToGuiceProviderAdapter(returnData);
        }
    }
}