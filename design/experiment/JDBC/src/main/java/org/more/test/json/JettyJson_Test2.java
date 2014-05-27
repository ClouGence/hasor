/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.test.json;
import java.io.IOException;
import java.util.Map;
import org.more.json.JSON;
public class JettyJson_Test2 {
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        Map<String, String> data = (Map<String, String>) JSON.parse("{'attr':'attValue','attr2':'attValue'}");
        System.out.println(data.get("attr"));
        System.out.println(data.get("attr2"));
    }
}