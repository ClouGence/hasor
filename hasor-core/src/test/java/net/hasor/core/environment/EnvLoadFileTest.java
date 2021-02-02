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
package net.hasor.core.environment;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnvLoadFileTest {
    @Test
    public void envTest0() throws IOException {
        System.setProperty("CONFLICTS_VAR", "with app");
        StandardEnvironment env = null;
        //
        env = new StandardEnvironment(null, "/net_hasor_core_environment/simple-config.xml");
        assert "my my my".equals(env.evalString("%MY_ENV%"));
        assert "with app".equals(env.evalString("%CONFLICTS_VAR%"));
        //
        //
        Map<String, String> envMap = new HashMap<String, String>();
        envMap.put("CONFLICTS_VAR", "with env");
        env = new StandardEnvironment(null, "/net_hasor_core_environment/simple-config.xml", envMap, Thread.currentThread().getContextClassLoader());
        assert "with env".equals(env.evalString("%CONFLICTS_VAR%"));
    }
}
