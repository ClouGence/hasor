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
package org.platform.runtime.binder;
import org.platform.api.binder.AbstractApiBinder;
import org.platform.api.context.InitContext;
import com.google.inject.Binder;
/**
 * 
 * @version : 2013-4-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class SystemApiBinder extends AbstractApiBinder {
    private Binder guiceBinder = null;
    public SystemApiBinder(InitContext initContext) {
        super(initContext);
    }
    @Override
    public Binder getGuiceBinder() {
        return this.guiceBinder;
    }
    public void setGuiceBinder(Binder guiceBinder) {
        this.guiceBinder = guiceBinder;
    }
}