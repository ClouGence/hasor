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
package org.test.more.core.util;
import java.util.Comparator;
import java.util.Map.Entry;
import org.junit.Test;
import org.more.core.iatt.Attribute;
import org.more.core.iatt.DecStackDecorator;
import org.more.util.MergeUtil;
@SuppressWarnings("unchecked")
public class Att {
    @Test
    public void test_Attribute() {
        Attribute aa = new Attribute();
        aa.setAttribute("a", "a1");
        aa.setAttribute("c", "c");
        System.out.println(aa);
        Attribute bb = new Attribute();
        bb.setAttribute("a", "a2");
        bb.setAttribute("b", "b");
        System.out.println(bb);
        //
        System.out.println(MergeUtil.mergeMap(aa, bb, new Comparator<Entry<Object, Object>>() {
            public int compare(Entry o1, Entry o2) {
                return -1;
            }
        }));;
    }
    @Test
    public void test_DecSequenceAttribute() {
        Attribute aa = new Attribute();
        aa.setAttribute("a", "a1");
        aa.setAttribute("c", "c");
        System.out.println(aa);
        Attribute bb = new Attribute();
        bb.setAttribute("a", "a2");
        bb.setAttribute("b", "b");
        System.out.println(bb);
        //
        DecSequenceAttribute sa = new DecSequenceAttribute();
        sa.putAtt(aa);
        sa.putAtt(bb);
        System.out.println(sa.getAttribute("a"));
    }
    @Test
    public void test_DecStackDecorator() {
        //
        //        DecParentAttribute pa = new DecParentAttribute(aa, bb);
        //
        DecStackDecorator stack = new DecStackDecorator();
        stack.setAttribute("age", 32);
        stack.setAttribute("name", 1);
        stack.createStack();
        stack.setAttribute("name", 2);
        stack.createStack();
        stack.setAttribute("name", 3);
        stack.createStack();
        stack.setAttribute("name", 4);
        System.out.println(stack.getAttribute("name"));
        System.out.println(stack.getAttribute("age"));
        System.out.println(stack.getDepth());
        System.out.println();
        stack.dropStack();
        System.out.println(stack.getAttribute("name"));
        System.out.println(stack.getAttribute("age"));
        System.out.println(stack.getDepth());
        System.out.println();
        stack.dropStack();
        System.out.println(stack.getAttribute("name"));
        System.out.println(stack.getAttribute("age"));
        System.out.println(stack.getDepth());
        System.out.println();
        stack.dropStack();
        System.out.println(stack.getAttribute("name"));
        System.out.println(stack.getAttribute("age"));
        System.out.println(stack.getDepth());
        System.out.println();
    }
}