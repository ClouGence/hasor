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
package net.hasor.core.provider;
import net.hasor.core.Provider;
/**
 * 单例对象的{@link Provider}封装形式。
 * @version : 2014年7月8日
 * @author 赵永春(zyc@hasor.net)
 */
public class SingleProvider<T> implements Provider<T> {
    private          Provider<T> provider = null;
    private volatile T           instance = null;
    private final    Object      lock     = new Object();
    //
    public SingleProvider(Provider<T> provider) {
        this.provider = provider;
    }
    public T get() {
        if (this.instance == null) {
            synchronized (this.lock) {
                if (this.instance == null) {
                    this.instance = this.provider.get();
                }
            }
        }
        return this.instance;
    }
    public String toString() {
        return "SingleProvider->" + provider.toString();
    }
}