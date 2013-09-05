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
package org.test.more.core.classcode;
import org.junit.Test;
import org.more.asm.ClassAdapter;
import org.more.asm.ClassWriter;
import org.more.classcode.BuilderMode;
import org.more.classcode.ClassBuilder;
import org.more.classcode.ClassEngine;
/**
 *
 * @version 2010-8-25
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
public class ExtTest {
    @Test
    public void test_1() throws Exception {
        ClassEngine ce = new TestClassEngine();
        ce.setBuilderMode(BuilderMode.Propxy);
        System.out.println(ce.newInstance(new Object()));
    };
}
class TestClassEngine extends ClassEngine {
    public TestClassEngine() throws ClassNotFoundException {
        super();
    }
    protected ClassBuilder createBuilder(BuilderMode builderMode) {
        if (builderMode == BuilderMode.Super)
            return new Super_TestClassBuilder();
        else
            return new Propxy_TestClassBuilder();
    }
}
class Super_TestClassBuilder extends ClassBuilder {
    protected ClassAdapter acceptClass(ClassWriter classVisitor) {
        System.out.println("Super_TestClassBuilder  acceptClass...");
        return null;
    }
    protected void init(ClassEngine classEngine) {
        System.out.println("Super_TestClassBuilder  init...");
    }
}
class Propxy_TestClassBuilder extends ClassBuilder {
    protected ClassAdapter acceptClass(ClassWriter classVisitor) {
        System.out.println("Propxy_TestClassBuilder  acceptClass...");
        return null;
    }
    protected void init(ClassEngine classEngine) {
        System.out.println("Propxy_TestClassBuilder  init...");
    }
}