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
package net.hasor.web.invoker.beans;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerData;
import net.hasor.web.WebPlugin;
/**
 * @version : 2017-01-08
 * @author 赵永春 (zyc@hasor.net)
 */
public class TestWebPlugin implements WebPlugin {
    private static boolean beforeFilter = false;
    private static boolean afterFilter  = false;
    //
    public static void resetInit() {
        beforeFilter = false;
        afterFilter = false;
    }
    public static boolean isBeforeFilter() {
        return beforeFilter;
    }
    public static boolean isAfterFilter() {
        return afterFilter;
    }
    //
    @Override
    public void beforeFilter(Invoker invoker, InvokerData info) {
        beforeFilter = true;
    }
    @Override
    public void afterFilter(Invoker invoker, InvokerData info) {
        afterFilter = true;
    }
}