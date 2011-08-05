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
package org.test.more.submit;
import org.junit.Test;
import org.more.submit.ActionObject;
import org.more.submit.SubmitBuild;
import org.more.submit.SubmitService;
import org.more.submit.acs.guice.AC_Guice;
/**
 * @version 2010-10-18
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class Test_2 {
    @Test
    public void test() throws Throwable {
        test_0();
    }
    public void test_0() throws Throwable {
        System.out.println("start...");
        //
        SubmitBuild build = new SubmitBuild();
        SubmitService submit = build.build();
        submit.regeditNameSpace("guice", new AC_Guice());
        //
        ActionObject v_1 = submit.getActionObject("guice://org.test.more.submit.ActionBean_1.testAction_1/");
        //ActionObject v_2 = submit.getActionObject("x://org.test.more.submit.ActionBean_1.testAction_2/");
        //ActionObject v_3 = submit.getActionObject("a://ta1/");
        //ActionObject v_4 = submit.getActionObject("a://ta2/");
        //
        //
        System.gc();
    }
}