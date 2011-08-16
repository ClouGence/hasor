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
package org.test.more.rmi;
import java.rmi.Naming;
import org.test.more.rmi.service.Faces;
/**
 * 
 * @version : 2011-8-16
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class RmiClient {
    public static void main(String[] args) throws Throwable {
        String url = "rmi://localhost:1099//faces";
        Object obj = Naming.lookup(url);
        System.out.println(obj instanceof Faces);
        Faces ss = (Faces) obj;
        ss.print("hello");
    }
}
