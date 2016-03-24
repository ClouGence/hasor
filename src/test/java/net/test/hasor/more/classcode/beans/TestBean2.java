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
package net.test.hasor.more.classcode.beans;
/**
 * 
 * @version 2011-6-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class TestBean2 implements TestBean2_Face {
    private long    p_long    = 0;
    private float   p_float   = 0;
    private int     p_double  = 0;
    private boolean p_boolean = false;
    //
    public long getP_long() {
        return p_long;
    }
    public void setP_long(long p_long) {
        this.p_long = p_long;
    }
    public float getP_float() {
        return p_float;
    }
    public void setP_float(float p_float) {
        this.p_float = p_float;
    }
    public int getP_double() {
        return p_double;
    }
    public void setP_double(int p_double) {
        this.p_double = p_double;
    }
    public Boolean isP_boolean() {
        return p_boolean;
    }
    public void setP_boolean(Boolean p_boolean) {
        this.p_boolean = p_boolean;
    }
}