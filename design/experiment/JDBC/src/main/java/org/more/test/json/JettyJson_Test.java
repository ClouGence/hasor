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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.more.json.JSON;
public class JettyJson_Test {
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        StringBuffer sb = new StringBuffer();
        FileReader fr = new FileReader("src/main/resources/org/more/test/json/jsonException.txt");
        BufferedReader bf = new BufferedReader(fr);
        String str = null;
        while ((str = bf.readLine()) != null)
            sb.append(str);
        Object obj = JSON.parse(sb.toString());
        System.out.println(obj);
        // TODO Auto-generated method stub
    }
}