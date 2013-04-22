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
package org.more.global.assembler.propertie;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.more.global.GlobalFactory;
import org.more.util.map.Properties;
/**
* 使用{@link Properties}类，装载属性文件。
* @version : 2011-9-3
* @author 赵永春 (zyc@byshell.org)
*/
public class PropertiesGlobalFactory extends GlobalFactory {
    protected Map<String, Object> loadConfig(InputStream stream, String encoding) throws IOException {
        Properties prop = new Properties();
        prop.load(new InputStreamReader(stream, encoding));
        if (stream != null)
            prop.load(stream);
        return new HashMap<String, Object>(prop);
    }
};