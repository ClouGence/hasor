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
import org.more.submit.SubmitBuild;
import org.more.submit.SubmitContext;
import org.more.submit.casing.more.MoreBuilder;
import org.more.util.StringConvert;
public class MoreSubmit30Test {
    /**
     * @param args
     * @throws Throwable
     * @throws Exception
     */
    public static void main(String[] args) throws Throwable {
        SubmitBuild dire = new SubmitBuild();
        dire.build(new MoreBuilder("test_src/org/test/more/submit/demo-beans-config.xml"));
        SubmitContext context = dire.getResult();
        context.doAction("test.xml", StringConvert.parseMap("\"\', 11, 33, false"));
    }
}