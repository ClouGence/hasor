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
package net.hasor.db.mapping;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.io.IOUtils;
import org.junit.Test;

import java.io.IOException;

/***
 *
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class XmlMappingTest extends AbstractDbTest {
    private String loadString(String queryConfig) throws IOException {
        return IOUtils.readToString(ResourcesUtils.getResourceAsStream(queryConfig), "UTF-8");
    }

    @Test
    public void mapperTest_01() throws Exception {
        String mapperString = loadString("/net_hasor_db/dal_dynamic/result_map/mapper_1.xml");
        //
        TableMapping tableMapping = MappingRegistry.DEFAULT.resolveTableMapping("mapperTest_01", mapperString);
        System.out.println(tableMapping);
    }

    @Test
    public void mapperTest_02() throws Exception {
        String mapperString = loadString("/net_hasor_db/dal_dynamic/result_map/mapper_2.xml");
        //
        TableMapping tableMapping = MappingRegistry.DEFAULT.resolveTableMapping("mapperTest_02", mapperString);
        System.out.println(tableMapping);
    }
}
