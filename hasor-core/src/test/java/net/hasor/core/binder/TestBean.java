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
package net.hasor.core.binder;
import java.lang.reflect.Method;
import java.util.Date;
/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class TestBean {
    private Date   abc1;
    private int    abc2;
    private Object abc3;
    private Method abc4;
    //
    public Date getAbc1() {
        return abc1;
    }
    public void setAbc1(Date abc1) {
        this.abc1 = abc1;
    }
    public int getAbc2() {
        return abc2;
    }
    public void setAbc2(int abc2) {
        this.abc2 = abc2;
    }
    public Object getAbc3() {
        return abc3;
    }
    public void setAbc3(Object abc3) {
        this.abc3 = abc3;
    }
    public Method getAbc4() {
        return abc4;
    }
    public void setAbc4(Method abc4) {
        this.abc4 = abc4;
    }
    //
    public void doInit() {
    }
}