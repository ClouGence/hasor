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
package org.hasor.icache.support;
import org.hasor.MoreFramework;
import org.hasor.context.AppContext;
import org.hasor.icache.KeyBuilder;
import com.google.inject.Key;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-4-216
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class KeyBuilderDefinition implements Provider<KeyBuilder> {
    private Class<?>                  type             = null;
    private Key<? extends KeyBuilder> keyBuilderKey    = null;
    private KeyBuilder                keyBuilderObject = null;
    //
    public KeyBuilderDefinition(Class<?> type, Key<? extends KeyBuilder> keyBuilderKey) {
        this.type = type;
        this.keyBuilderKey = keyBuilderKey;
    }
    public Key<? extends KeyBuilder> getKeyBuilderKey() {
        return this.keyBuilderKey;
    }
    public void initKeyBuilder(final AppContext appContext) {
        MoreFramework.info("initKeyBuilder %s mappingTo %s.", type, keyBuilderKey);
        this.keyBuilderObject = appContext.getGuice().getInstance(this.keyBuilderKey);
        this.keyBuilderObject.initKeyBuilder(appContext);
    }
    public boolean canSupport(Class<?> targetType) {
        if (targetType == null)
            return false;
        return this.type.isAssignableFrom(targetType);
    }
    @Override
    public KeyBuilder get() {
        return this.keyBuilderObject;
    }
    public void destroy(AppContext appContext) {
        if (this.keyBuilderObject != null)
            this.keyBuilderObject.destroy(appContext);
    }
}