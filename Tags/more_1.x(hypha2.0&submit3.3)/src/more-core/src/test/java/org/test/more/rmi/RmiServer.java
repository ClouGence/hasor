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
import org.more.hypha.context.app.DefaultApplicationContext;
import org.more.hypha.context.xml.XmlDefineResource;
public class RmiServer {
    public static void main(String[] args) throws Throwable {
        System.out.println("start...");
        XmlDefineResource xdr = new XmlDefineResource();
        //
        xdr.addSource("org/test/more/rmi/rmi-config.xml");
        xdr.loadDefine();
        //
        DefaultApplicationContext app = new DefaultApplicationContext(xdr);
        app.init();
        System.out.println("----------------------------------");
    }
}