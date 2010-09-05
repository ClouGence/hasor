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
package org.test.more.classcode;
import org.junit.Test;
import org.more.core.classcode.BuilderMode;
import org.more.core.classcode.ClassBuilder;
import org.more.core.classcode.ClassEngine;
/**
 *
 * @version 2010-8-25
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class ExtTest {
    @Test
    public void test_1() throws Exception {
        ClassEngine ce = new ClassEngine();
        System.out.println(ce.newInstance(null));
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
class Super_TestClassBuilder extends ClassBuilder {}
class Propxy_TestClassBuilder extends ClassBuilder {}