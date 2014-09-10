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
package net.test.simple.core._14_aop;
//
public class Bean {
    public String print(int i, int c) {
        System.out.println();
        return "";
    }
    //    public int doCall(int abc, Object abcc, Class classType, Date date, Long lon) {
    //        System.out.println();
    //        return abc;
    //    }
    //    public native long add(int abc, Long lon);
    //    //
    //    public <T extends List<Map<Integer, X>>, Y> void rem(T aa, Y abc) {
    //        // TODO Auto-generated method stub
    //    }
}
// <T::Ljava/util/List<Ljava/util/Map<Ljava/lang/Integer;TX;>;>;>(TT;Ljava/lang/Long;)V
// <T::Ljava/util/List<Ljava/util/Map<Ljava/lang/Integer;TX;>;>;>(TT;Ljava/lang/Long;)V
//interface IBean<V, X> {
//    public <T extends List<Map<Integer, X>>, Y> void rem(T aa, Y abc);
//}