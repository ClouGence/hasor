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
package net.test.simple._02_beans.singleton;
import javax.inject.Singleton;
/**
 * µ•¿˝
 * @version : 2013-8-11
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
@Singleton
public class SingletonBean {
    private long time = 0;
    //
    public SingletonBean() {
        time = System.currentTimeMillis();
    }
    //
    public void foo() {
        System.out.println("create at time:" + time);
    }
}