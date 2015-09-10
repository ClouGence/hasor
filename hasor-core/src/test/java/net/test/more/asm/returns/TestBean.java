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
package net.test.more.asm.returns;
/**
 * 
 * @version : 2013-9-20
 * @author 赵永春 (zyc@byshell.org)
 */
@TestAnno
public class TestBean {
    public <T> String halloAop(T abc, int aaa) throws Throwable, Exception {
        return null;
    }
    public int returnInteger() {
        int val = 45;
        return val;
    }
    public void returnLong() {
        System.out.println("Hello Aop");
    }
    public void returnDouble() {
        System.out.println("Hello Aop");
    }
}