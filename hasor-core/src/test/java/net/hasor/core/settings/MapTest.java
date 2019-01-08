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
import net.hasor.core.setting.DecSpaceMap;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
/**
 *
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class MapTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    // - 配置信息读取
    @Test
    public void mapTest() {
        DecSpaceMap<String, String> spaceMap = new DecSpaceMap<String, String>();
        spaceMap.putAll("aaa", new HashMap<String, String>());
        spaceMap.putAll("bbb", new HashMap<String, String>());
        //
        spaceMap.put("aaa", "key_a", "k1");
        spaceMap.put("aaa", "key_b", "k2");
        spaceMap.put("aaa", "key_c", "k3");
        //
        spaceMap.put("bbb", "key_b", "k4");
        spaceMap.put("bbb", "key_c", "k5");
        spaceMap.put("bbb", "key_d", "k6");
        spaceMap.put("bbb", "key_e", "k7");
        //
        assert spaceMap.remove("ccc", "key_a") == null;
        assert spaceMap.keySet("ccc").size() == 0;
        //
        assert spaceMap.size() == 7;
        assert spaceMap.size("aaa") == 3;
        assert spaceMap.size("bbb") == 4;
        assert spaceMap.size("ccc") == 0;
        //
        Set<String> spaceSet = spaceMap.spaceSet();
        assert spaceSet.contains("aaa") && spaceSet.contains("bbb") && spaceSet.size() == 2;
        //
        Set<String> keys1 = spaceMap.keySet();
        assert keys1.contains("key_a") && keys1.contains("key_b") && keys1.contains("key_c") && keys1.contains("key_d");
        Set<String> keys2 = spaceMap.keySet("aaa");
        assert keys2.contains("key_a") && keys2.contains("key_b") && keys2.contains("key_c") && !keys2.contains("key_d");
        Set<String> keys3 = spaceMap.keySet("bbb");
        assert !keys3.contains("key_a") && keys3.contains("key_b") && keys3.contains("key_c") && keys3.contains("key_d");
        //
        List<String> keyA = spaceMap.get("key_a");
        assert keyA.size() == 1 && keyA.contains("k1");
        List<String> keyB = spaceMap.get("key_b");
        assert keyB.size() == 2 && keyB.contains("k2") && keyB.contains("k4");
        //
        assert spaceMap.remove("aaa", "key_b") != null;
        assert spaceMap.size() == 6;
        Set<String> valuesA = spaceMap.valueSet("aaa");
        assert valuesA.size() == 2 && valuesA.contains("k1") && valuesA.contains("k3");
        //
        spaceMap.removeAll("key_c");
        Set<String> valueSet = spaceMap.valueSet();
        assert spaceMap.size() == 4 && valueSet.size() == 4;
        assert valueSet.contains("k1") && valueSet.contains("k4") && valueSet.contains("k6") && valueSet.contains("k7");
        //
        assert spaceMap.valueSet("ccc").size() == 0;
        spaceMap.deleteSpace("bbb");
        valueSet = spaceMap.valueSet();
        assert spaceMap.size() == 1 && valueSet.size() == 1;
        assert valueSet.contains("k1");
        //
        spaceMap.deleteAllSpace();
        assert spaceMap.size() == 0;
    }
}