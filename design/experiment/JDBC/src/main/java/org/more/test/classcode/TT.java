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
package org.more.test.classcode;
import org.more.classcode.AopFilterChain_Start;
import org.more.classcode.Method;
public class TT {
    private long                   p_long;
    private float                  p_float;
    private int                    p_double;
    private Boolean                $configMark;
    private AopFilterChain_Start[] $aopFilterChain;
    private Method[]               $aopMethods;
    public long getP_long(int p1, boolean p2, Object p3, int p4, boolean p5, Object p6) {
        if (this.$aopFilterChain == null)
            return $aopFungetP_long();
        Object[] params = new Object[] { p1, p2, p3, p4, p5, p6 };
        return ((Long) this.$aopFilterChain[0].doInvokeFilter(this, this.$aopMethods[0], params)).longValue();
    }
    private long $aopFungetP_long() {
        // TODO Auto-generated method stub
        return 0;
    }
}
