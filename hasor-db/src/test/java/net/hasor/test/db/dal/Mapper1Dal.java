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
package net.hasor.test.db.dal;
import net.hasor.db.dal.RefMapper;
import net.hasor.test.db.dto.TbUser;

import java.util.List;

/**
 *
 * @version : 2013-12-10
 * @author 赵永春 (zyc@hasor.net)
 */
@RefMapper("/net_hasor_db/dal_dynamic/mapper_1.xml")
public interface Mapper1Dal {
    public List<TbUser> testBind(String abc);

    public List<TbUser> testChoose(String title, String content);

    public List<TbUser> testForeach(List<String> eventTypes);

    public List<TbUser> testIf(String ownerID, String ownerType);
}
