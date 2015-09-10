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
package net.test.more.classcode;
/**
 * 
 * @version 2011-6-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class TestBean {
    public long  p_long   = 0;
    public float p_float  = 0;
    public int   p_double = 0;
    //    public TestBean(long p_long, float p_float) {
    //        this.p_long = p_long;
    //        this.p_float = p_float;
    //    };
    public void setLong(Long p_long) {
        this.p_long = p_long;
    };
    //    public void setDouble(int p_double) {
    //        this.p_double = p_double;
    //    };
}
class TB extends TestBean {
    //    public TB(long p_long, float p_float) {
    //        super(p_long, p_float);
    //    }
    public void setLong(long p_long) {
        super.setLong(p_long);
    };
}