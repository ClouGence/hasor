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
import org.more.core.remote.DefaultPublisher;
import org.test.more.rmi.service.FacesImpl;
/**
 * 
 * @version : 2012-2-9
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class RmiServer {
    public static void main(String[] args) throws Throwable {
        DefaultPublisher push = new DefaultPublisher("127.0.0.1", 880);
        push.pushObject("faces", new FacesImpl());
        push.start();
        System.out.println("----------------------------------");
        for (String pushObj : push.getPushList())
            System.out.println(pushObj);
    }
}