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
package org.hasor.test.dependency;
import java.io.IOException;
import org.hasor.context.anno.context.AnnoAppContextSupportModule;
public class Dep1Test {
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        /*
         * Mode1
         *   Mode2
         *     Mode4
         *       Mode5
         *   Mode3
         *     Mode4
         *       Mode5
         *     Mode6
         *       Mode7
         *       Mode8
         *         Mode1 **
         *   Mode9
         */
        AnnoAppContextSupportModule annoApp = new AnnoAppContextSupportModule("dep1-config.xml");
        annoApp.start();
        // TODO Auto-generated method stub
        annoApp.destroy();
    }
}