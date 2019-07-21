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
package net.hasor.core.settings;
import net.hasor.core.setting.InputStreamSettings;
import net.hasor.core.setting.StreamType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 *
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class LoadTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    // - 配置信息读取
    @Test
    public void mapTest() throws IOException {
        InputStreamSettings inputStreamSettings = new InputStreamSettings();
        assert inputStreamSettings.loadSettings() == 0;
        //
        StringReader ins = new StringReader(new String(new byte[] { 0, 0, 0 }));
        assert inputStreamSettings.addReader(ins, StreamType.Xml);
        assert !inputStreamSettings.addReader(ins, StreamType.Xml);
        assert !inputStreamSettings.addReader(null, StreamType.Xml);
        assert !inputStreamSettings.addReader(null, null);
        assert !inputStreamSettings.addReader(ins, null);
        //
        try {
            inputStreamSettings.loadSettings();
            assert false;
        } catch (IOException e) {
            assert e.getMessage().startsWith("parsing failed -> ");
        }
    }
}