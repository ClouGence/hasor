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
package net.hasor.core.aop;
import org.junit.Test;

public class AopToosTest {
    @Test
    public void toosTest1() throws Exception {
        String[] asmType1 = AsmTools.splitAsmType("IIIILLjava/lang/Integer;");
        assert asmType1[0].equals("I");
        assert asmType1[1].equals("I");
        assert asmType1[2].equals("I");
        assert asmType1[3].equals("I");
        assert asmType1[4].equals("LLjava/lang/Integer;");
        //
        String[] asmType2 = AsmTools.splitAsmType("");
        assert asmType2.length == 0;
    }
}